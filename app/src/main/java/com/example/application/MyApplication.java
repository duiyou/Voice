package com.example.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.example.activity.ActivityCollector;
import com.example.activity.BaseActivity;
import com.example.activity.chat.ChatActivity;
import com.example.activity.memo.MemoActivity;
import com.example.activity.translate.TranslateActivity;
import com.example.activity.updatememo.UpdateMemoActivity;
import com.example.util.DeployMessageUtil;
import com.example.util.SpeechWakeUpUtil;
import com.example.voice.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * 自定义Application
 * 为了获取全局Context
 * 参考：https://www.jianshu.com/p/802b833b8c36
 *
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    //全局Context
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i(TAG, "onCreate: 初始化了Myapplication类");
        //初始化所有东西
        init();

    }

    //初始化所有东西
    private void init() {
        //配置信息工具类配置语音合成配置信息
        DeployMessageUtil.initSpeechSynthesisMessage();
        //初始化唤醒类
        initWakeUpUtil();
    }

    //初始化唤醒类
    private SpeechWakeUpUtil mSpeechWakeUpUtil;
    private void initWakeUpUtil(){
        mSpeechWakeUpUtil = SpeechWakeUpUtil.getInstance(this.context, new MySpeechWakeUpListener());
        mSpeechWakeUpUtil.startWakeUp();
        Log.i(TAG, "initWakeUpUtil: 初始化了唤醒类");
    }

    //自定义语音唤醒监听内部类
    class MySpeechWakeUpListener implements EventListener {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            //唤醒时间
            if("wp.data".equals(name)){
                try {
                    JSONObject json = new JSONObject(params);
                    int errorCode = json.getInt("errorCode");
                    if(errorCode == 0){
                        //唤醒成功
                        Log.i(TAG, "onEvent: 唤醒成功了");
                        String wakeUpWord = json.getString("word");
                        Log.i(TAG, "onEvent: 唤醒词："+wakeUpWord);
                        //如果识别到这个唤醒词，则去改变这个活动
                        if(wakeUpWord != null && !wakeUpWord.isEmpty()){
                            changeActivity(wakeUpWord);
                        }
                    }else{
                        Log.e(TAG, "onEvent: 唤醒失败了");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if("wp.exit".equals(name)){
                Log.i(TAG, "onEvent: 已经停止唤醒了");
            }
        }
    }

    //只能从ChatActivity活动跳转其他活动
    private ChatActivity chatActivity = null;

    //跳转Activity
    private void changeActivity(String wakeUpWord){
        //先初始化主活动，其他活动都只能从主活动跳转
        if (chatActivity == null) {
            for (Activity temp : ActivityCollector.activities) {
                if (temp.getClass().equals(ChatActivity.class)) {
                    chatActivity = (ChatActivity)temp;
                }
            }
        }
        /*ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
        //获取当前Activity类的完整路径
        String className = info.topActivity.getShortClassName();
        Log.d(TAG, "changeActivity: 当前的活动类为：" + className);*/
        //用来记住当前的Activity
        Activity nowActivity = null;
        switch(wakeUpWord){
            case "小白聊天":
                //只有找到该活动对象，并且正在显示时才调用语音按钮，其他情况都是跳转
                for (BaseActivity temp : ActivityCollector.activities) {
                    if (temp.getClass().equals(ChatActivity.class)) {
                        if (((ChatActivity)temp).isShow) {
                            ((ChatActivity)temp).pressButton();
                            return;
                        }
                    }
                }
                //其他情况都是跳转到ChatActivity
                ChatActivity.actionStart(chatActivity);
                //由于是单例，所以chatActivity会弹出所有在他之上的活动，即都finish了
                break;
            case "小白翻译":
                //只有找到该活动对象，并且正在显示时才调用语音按钮，其他情况都是跳转
                for (BaseActivity temp : ActivityCollector.activities) {
                    if (temp.isShow) nowActivity = temp;
                    if (temp.getClass().equals(TranslateActivity.class)) {
                        if (((TranslateActivity)temp).isShow) {
                            ((TranslateActivity)temp).pressButton();
                            return;
                        }
                    }
                }
                //如果不是当前程序中唤醒，则把除了chat以外的都销毁
                if (nowActivity == null) {
                    for (BaseActivity temp : ActivityCollector.activities) {
                        if (!temp.getClass().equals(ChatActivity.class)) {
                            temp.finish();
                        }
                    }
                }
                //其他情况都是跳转到ChatActivity
                TranslateActivity.actionStart(chatActivity);
                //只有是当前程序才进行之后的判断，不是的话之前就已经清空了
                if (nowActivity != null) {
                    //不允许不是主活动跳转到当前活动，所以正在显示的不是主活动则销毁该活动
                    if (!nowActivity.getClass().equals(ChatActivity.class)) {
                        nowActivity.finish();
                    }
                    //如果此时是修改备忘界面，则还要把备忘录界面给finish()
                    if (nowActivity.getClass().equals(UpdateMemoActivity.class)) {
                        for (BaseActivity temp : ActivityCollector.activities) {
                            if (temp.getClass().equals(MemoActivity.class)) {
                                temp.finish();
                                break;
                            }
                        }
                    }
                }
                break;
            case "小白备忘":
                //只有找到该活动对象，并且正在显示时才调用语音按钮，其他情况都是跳转
                for (BaseActivity temp : ActivityCollector.activities) {
                    if (temp.isShow) nowActivity = temp;
                    if (temp.getClass().equals(MemoActivity.class)) {
                        if (((MemoActivity)temp).isShow) {
                            ((MemoActivity)temp).pressButton();
                            return;
                        }
                    }
                }
                //如果不是当前程序中唤醒，则把除了chat以外的都销毁
                if (nowActivity == null) {
                    for (BaseActivity temp : ActivityCollector.activities) {
                        if (!temp.getClass().equals(ChatActivity.class)) {
                            temp.finish();
                        }
                    }
                }
                //其他情况都是跳转到ChatActivity
                MemoActivity.actionStart(chatActivity);
                //不允许不是主活动跳转到当前活动，所以正在显示的不是主活动则销毁该活动
                if (nowActivity != null && !nowActivity.getClass().equals(ChatActivity.class)) {
                    nowActivity.finish();
                }
                break;
        }
    }
}

package com.example.util;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 语音唤醒工具类
 * 参考：
 * https://blog.csdn.net/zhangyonggang886/article/details/52782799
 */

public class SpeechWakeUpUtil {
    private static final String TAG = "SpeechWakeUpUtil";

    private Context context;

    //功能类
    private EventManager mEventManager;
    //监听
    private EventListener e;

    //单例
    public static SpeechWakeUpUtil instance;
    public static synchronized SpeechWakeUpUtil getInstance(Context context, EventListener e){
        if(instance == null)instance = new SpeechWakeUpUtil(context, e);
        return instance;
    }

    //私有构造器
    private SpeechWakeUpUtil(Context context, EventListener e){
        init(context , e);
    }
    private void init(Context context, EventListener e){
        this.context = context;
        this.e = e;
        mEventManager = EventManagerFactory.create(context, "wp");
        mEventManager.registerListener(e);
    }
    //开启唤醒
    public void startWakeUp(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        mEventManager.send(SpeechConstant.WAKEUP_START, new JSONObject(params).toString(), null, 0, 0);
        Log.d(TAG, "startWakeUp: 开启唤醒啦");
    }
    //停止唤醒
    public void stopWakeUp(){
        mEventManager.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    //始放资源
    public void release(){
        //先停止
        stopWakeUp();
        Log.d(TAG, "startWakeUp: 结束唤醒啦");
        mEventManager.unregisterListener(e);
        if(instance != null)instance = null;
    }
}

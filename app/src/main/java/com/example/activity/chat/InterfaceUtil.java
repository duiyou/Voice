package com.example.activity.chat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.util.ThirdAPPSkipUtil;
import com.example.voice.R;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;


public class InterfaceUtil {
    private static final String TAG = "InterfaceUtil";

    private ChatActivity chatActivity;

    //单例
    public static InterfaceUtil instance;
    public static synchronized InterfaceUtil getInstance(ChatActivity chatActivity){
        if(instance == null)instance = new InterfaceUtil(chatActivity);
        return instance;
    }
    //私有构造器
    private InterfaceUtil(ChatActivity chatActivity){
        this.chatActivity = chatActivity;
        //初始化所有需要用的东西
        init();
    }

    private void init(){
        //初始化后台工具类
        initBackgroundUtil();
        //初始化界面控件
        initComponent();
    }

    /**
     * 初始化所有控件
     */
    private void initComponent(){
        //初始化List控件
        initRecyclerView();
        //初始化语音按钮
        initSpeechButton();
        //初始化加载控件
        initMKLoader();
        //初始化进度条
        initProgressBar();
    }




    /*****************以下为后台工具类**********************/

    /**
     * 初始化后台工具类
     */
    public BackgroundUtil backgroundUtil;
    private void initBackgroundUtil(){
        backgroundUtil = BackgroundUtil.getInstance(chatActivity);
        backgroundUtil.setBackgroundUtilListener(new MyBackgroundUtilListener());
    }

    /**
     * 后台工具类监听
     */
    class MyBackgroundUtilListener implements BackgroundUtil.BackgroundUtilListener {

        //最后结果
        private String finalResult = null;

        //语义的domain
        private String domainResult = null;

        //可以开始说话
        @Override
        public void startSpeech() {
            //开始UI
            startUI();
        }

        //临时结果
        @Override
        public void finalRecoginze(String finalStr) {
            //把最后的结果存储下来
            finalResult = finalStr;
        }

        //得到语义的domain
        @Override
        public void domainRecognize(String doaminStr) {
            //把语义理解结果domain存储下来
            domainResult = doaminStr;
        }

        //说话结束了
        @Override
        public void endSpeech() {
            //结束UI
            stopUI();
            //结束说话时就显示加载
            showMKLoader();
        }

        //完成识别了
        @Override
        public void wellRecognize() {
            //不管三七二十一，结束了就一定要停止UI
            stopUI();
            //完成识别就隐藏加载
            hideMKLoader();
            if(finalResult != null && !finalResult.isEmpty()){
                //先去更新界面
                notifyItemInserted(finalResult,1);
                //处理识别结果结果
                handleFinalResult();

            }else{
                //不去发送信息证明结束流程，重置
                reset();
                //提示用户没有说话
                Toast.makeText(chatActivity,"检测到用户没有说话",Toast.LENGTH_SHORT).show();
            }
            //识别完就设置为空
            finalResult = null;
            domainResult = null;
        }

        //机器人回复
        @Override
        public void wellRobotReply(String reply) {
            if(reply != null && !reply.isEmpty()){
                Log.i(TAG, "wellTranslate: 回复结束了");
                //由于翻译时开启了线程，所以必须异步操作控件
                //为Message传入信息，参考https://blog.csdn.net/qq_37321098/article/details/81535449
                Bundle bundle = new Bundle();
                //传入key，value
                bundle.putString(replyResultKey, reply);
                Message message = Message.obtain();
                message.setData(bundle);
                message.what = SHOW_CHAT_REPLY;
                mHandler.sendMessage(message);

                /**************翻译完以后让语音合成啊******************/
                if(backgroundUtil != null)backgroundUtil.speak(reply);
            }else{
                /***********翻译出错****************/
                Log.e(TAG, "wellTranslate: 回复为空了，哪里出错了");
            }
            //不管有没有错误，都要重置，因为语音合成不管重置的事情了，不会影响
            reset();
        }

        //识别结束了，有识别结果时处理结果，有“打开”关键字就判断能否打开，不能再继续判断domain
        private void handleFinalResult(){
            //如果说的话是“小白语音指令”
            if (finalResult.indexOf("小白使用指令") != -1) {
                //显示提示信息
                showHint();
                return;
            }

            int index = finalResult.indexOf("打开");
            //有打开这个关键词就去判断打开什么东西
            if (index != -1) {
                Log.i(TAG, "wellRecognize: 打开的下标" + index);
                index += 2;
                String appName = finalResult.substring(index);
                Log.i(TAG, "handleResult: 打开啥东西：" + appName);
                //让他跳转，成功就啥也不做，不成功就继续语义结果
                if (!ThirdAPPSkipUtil.skipNameAPP(appName)) {
                    handleDomainResult();
                }
            } else {
                //没有打开的关键字啊，就处理语义结果啊
                handleDomainResult();
            }
        }

        //判断是否可以根据domain打开，有就打开，没有就和机器人对话了
        private void handleDomainResult() {
            //有语义结果了并且还能跳转，就啥也不用干
            if (domainResult != null && !domainResult.isEmpty()
                    && ThirdAPPSkipUtil.skipDomainAPP(chatActivity, domainResult)) {

            } else {    //否则就是跳转不了，和机器人聊天即可
                //和机器人聊天
                if(backgroundUtil != null){
                    backgroundUtil.sendMessage(finalResult);
                }
            }
        }
    }

    /**
     * 显示此软件的提示
     */
    private void showHint() {
        Bundle bundle = new Bundle();
        //传入key，value
        bundle.putString(replyResultKey, chatActivity.getResources().getString(R.string.str_how_to_use1));
        Message message = Message.obtain();
        message.setData(bundle);
        message.what = SHOW_CHAT_REPLY;
        mHandler.sendMessage(message);

        Bundle bundle2 = new Bundle();
        //传入key，value
        bundle2.putString(replyResultKey, chatActivity.getResources().getString(R.string.str_how_to_use2));
        Message message2 = Message.obtain();
        message2.setData(bundle2);
        message2.what = SHOW_CHAT_REPLY;
        mHandler.sendMessage(message2);
    }





    /*****************以下为List控件**********************/

    //适配器的信息集合
    private List<Msg> list = new ArrayList<>();
    //适配器
    private MsgAdapter msgAdapter;
    //滚动控件RecyclerView
    private RecyclerView recyclerView;

    /**
     *初始化List控件
     */
    private void initRecyclerView(){
        if(chatActivity != null){
            //初始化滚动控件
            recyclerView = (RecyclerView)chatActivity.findViewById(R.id.chat_activity_list);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(chatActivity);
            if(recyclerView != null)recyclerView.setLayoutManager(mLayoutManager);
            msgAdapter = new MsgAdapter(list);
            if(recyclerView != null)recyclerView.setAdapter(msgAdapter);
            //先提示一个语句
            notifyItemInserted(chatActivity.getResources().getString(R.string.str_chat_hint), 0);
        }
    }

    /**
     * 刷新滚动控件，显示最后一行
     * @param content
     * @param type
     */
    public void notifyItemInserted(String content,int type){
        //创建Msg并传入类型
        Msg mMsg = new Msg(content,type);
        //添加到集合（因为传入的就是这个集合，所以集合add以后另外一边也是同样的mList，刷新即可）
        list.add(mMsg);
        //刷新滚动控件
        msgAdapter.notifyItemInserted(list.size() - 1);
        //显示到最后一行
        recyclerView.scrollToPosition(list.size() - 1);
    }




    /*****************以下为语音按钮控件**********************/

    //语音按钮
    private ImageButton speechButton;
    //是否按了按钮（准备好说话才表示按了）
    private boolean isPress = false;
    //是否正在识别，默认不在识别状态
    private boolean isRecognizing = false;
    //表示正在工作
    private boolean isWorking = false;

    /**
     * 初始化语音按钮
     */
    private void initSpeechButton(){
        if(chatActivity != null){
            speechButton = chatActivity.findViewById(R.id.chat_activity_speechbutton);
            if(speechButton != null){
                speechButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //就一个按键监听
                        pressButton();
                    }
                });
            }
        }
    }

    /**
     * 点击语音按钮调用的方法
     */
    public void pressButton(){
        if(!isPress){
            if(backgroundUtil != null){
                //从按下那一刻开始就开始工作了
                isWorking = true;
                backgroundUtil.startRecognize(BackgroundUtil.SPEECH_CHINESE);
                //此时表示按了按钮
                isPress = true;
            }
        }else{
            //按下了按钮，此时正在录音了才能stop，否则啥也干不了
            if(isRecognizing){
                if(backgroundUtil != null){
                    backgroundUtil.stopRecognize();
                }
                //停止语音直接设为没有按按钮
                isPress = false;
                //停止语音后结束UI
                stopUI();
                //按了语音停止了以后就直接显示加载控件
                showMKLoader();
            }

        }
    }






    /*****************以下为加载控件**********************/

    /**
     * 初始化加载进度条控件
     */
    private MKLoader mkLoader;
    private void initMKLoader(){
        if(chatActivity != null){
            mkLoader = chatActivity.findViewById(R.id.chat_activity_mkloader);
        }
    }

    /**
     * 显示加载控件
     */
    private void showMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.GONE);
            mkLoader.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏加载控件
     */
    private void hideMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.VISIBLE);
            mkLoader.setVisibility(View.GONE);
        }
    }





    /*****************以下为进度条控件**********************/

    //进度条
    private SmoothProgressBar progressBar;

    /**
     * 初始化进度条
     */
    private void initProgressBar(){
        if(chatActivity != null){
            progressBar = chatActivity.findViewById(R.id.chat_activity_progressbutton);
            if(progressBar != null){
                //一开始不知道为啥就是要运行动画，无奈只好设置不可见并且再停止进度条了
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.progressiveStop();
                //颜色数组
                int[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE};
                //他的代码，没怎么看懂，不推荐修改！！！！！！
                progressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(chatActivity)
                        .colors(colors)
                        .interpolator(new DecelerateInterpolator())
                        .sectionsCount(4)
                        .separatorLength(8)         //You should use Resources#getDimensionPixelSize
                        .strokeWidth(8f)            //You should use Resources#getDimension
                        .speed(2f)                 //2 times faster
                        .progressiveStartSpeed(2)
                        .progressiveStopSpeed(10f)
                        .reversed(false)
                        .mirrorMode(false)
                        .progressiveStart(false)
                        .build());
            }
        }
    }





    /*****************以下为其他方法**********************/

    //UI是否开始了
    private boolean isUIStart = false;

    /**
     * 开始变化UI
     */
    private void startUI(){
        //设置可见啊
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }
        //开始UI一定正在录音了
        isRecognizing = true;
        //开始进度条和换图片(UI没开始再开始好吗）
        if(!isUIStart){
            isUIStart = true;
            if(progressBar != null)progressBar.progressiveStart();
            if (speechButton != null) {
                speechButton.setImageResource(R.mipmap.chat_activity_audio1);
                speechButton.setBackground(chatActivity.getResources().getDrawable(R.drawable.chat_activity_speechbutton_backgroundpress));
            }
        }
    }

    /**
     * 停止UI变化
     */
    private void stopUI(){
        //按钮没按下了
        isPress = false;
        //不在录音了
        isRecognizing = false;
        //停止进度条和改变按钮图片（UI开始了才能停止啊）
        if (isUIStart) {
            isUIStart = false;
            if (progressBar != null && !progressBar.isActivated()) progressBar.progressiveStop();
            if (speechButton != null) {
                speechButton.setImageResource(R.mipmap.chat_activity_audio2);
                speechButton.setBackground(chatActivity.getResources().getDrawable(R.drawable.chat_activity_speechbutton_backgrounddefault));
            }
        }
    }


    //翻译结果传入Bundle的key
    private static final String replyResultKey = "chatReply";

    //信息常量
    private static final int SHOW_CHAT_REPLY = 0X114;        //显示翻译结果

    /**
     * 异步处理线程
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            Bundle mBundle;
            switch (msg.what){
                //显示机器人回复信息
                case SHOW_CHAT_REPLY:
                    mBundle = msg.getData();
                    String translateResult = mBundle.getString(replyResultKey);
                    notifyItemInserted(translateResult,0);
                    break;
            }
        };
    };

    /**
     * 重置一些东西
     */
    private void reset(){
        isWorking = false;
    }

}

package com.example.activity.translate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.activity.BaseActivity;
import com.example.util.SpeechRecognitionUtil;
import com.example.util.SpeechSynthesisUtil;
import com.example.voice.R;


public class TranslateActivity extends BaseActivity {

    private static final String TAG = "TranslateActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity);
        //初始化所有信息
        init();
    }

    //初始化所有信息
    private void init(){
        //初始化界面工具类
        initInterfaceUtil();
    }

    /**
     * 初始化界面工具类
     */
    public InterfaceUtil interfaceUtil;
    private void initInterfaceUtil() {
        interfaceUtil = InterfaceUtil.getInstance(TranslateActivity.this);
    }

    /**
     * 这个活动按下了按钮，即开始语音识别
     */
    public void pressButton() {
        interfaceUtil.pressButton();
    }

    /**
     * 准备和用户交互时创建语音的对象类
     */
    @Override
    protected void onResume() {
        super.onResume();
        //显示了
        isShow = true;
        Log.i(TAG, "onStart: 显示了啊");
        if (interfaceUtil != null) {
            if (SpeechRecognitionUtil.instance == null) {
                interfaceUtil.backgroundUtil.initSpeechRecognitionUtil();
                Log.i(TAG, "onStart: 初始化语音识别");
            }
            if (SpeechSynthesisUtil.mSpeechSynthesisUtil == null) {
                interfaceUtil.backgroundUtil.initSpeechSynthesisUtil();
                Log.i(TAG, "onStart: 初始化语音合成");
            }

        }

    }

    /**
     * 准备启动其他活动时销毁语音对象类
     */
    @Override
    protected void onPause() {
        super.onPause();
        //活动不可见
        isShow = false;
        if(SpeechRecognitionUtil.instance != null){
            SpeechRecognitionUtil.instance.release();
            Log.d(TAG, "onStop: 始放语音识别");
        }
        if(SpeechSynthesisUtil.mSpeechSynthesisUtil != null){
            SpeechSynthesisUtil.mSpeechSynthesisUtil.Destpry();
            Log.d(TAG, "onStop: 销毁语音合成");
        }
    }

    /**
     * 销毁时工具类也直接销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (InterfaceUtil.instance != null) {
            InterfaceUtil.instance = null;
        }
        if (BackgroundUtil.instance != null) {
            BackgroundUtil.instance = null;
        }
        Log.e(TAG, "onDestroy: 销毁了");
        //销毁活动
        finish();
    }

    //开始这个活动
    public static void actionStart(Context context){
        Intent intent=new Intent(context, TranslateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}

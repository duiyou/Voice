package com.example.activity.translate;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.example.util.DeployMessageUtil;
import com.example.util.PropertiesUtil;
import com.example.util.SpeechRecognitionUtil;
import com.example.util.SpeechSynthesisUtil;
import com.example.util.TranslateManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class BackgroundUtil {
    private static final String TAG = "BackgroundUtil";

    //监听接口
    public interface BackgroundUtilListener{
        //可以开始说话
        void startSpeech();
        //得到最终结果
        void finalRecoginze(String finalStr);
        //结束说话
        void endSpeech();
        //完成语音识别
        void wellRecognize();
        //完成翻译
        void wellTranslate(String result);
    }
    private BackgroundUtilListener mListener;
    public void setBackgroundUtilListener(BackgroundUtilListener mListener){
        this.mListener = mListener;
    }

    private TranslateActivity translateActivity;

    //单例
    public static BackgroundUtil instance;
    public static synchronized BackgroundUtil getInstance(TranslateActivity translateActivity){
        if(instance == null)instance = new BackgroundUtil(translateActivity);
        return instance;
    }
    //私有构造器
    private BackgroundUtil(TranslateActivity translateActivity){
        this.translateActivity = translateActivity;
        //初始化所有需要用的东西
        init();
    }

    private void init() {
        //初始化语音识别类
        initSpeechRecognitionUtil();
        //初始化翻译类
        initTranslateManager();
        //初始化语音合成类
        initSpeechSynthesisUtil();
    }


    /***************以下为语音识别*****************************************/

    //识别方式常量
    public static final int SPEECH_CHINESE = SpeechRecognitionUtil.RECOGNITION_TYPE_CHINESE;
    public static final int SPEECH_ENGLISH = SpeechRecognitionUtil.RECOGNITION_TYPE_ENGLISH;

    /**
     * 初始化语音识别类
     */
    private SpeechRecognitionUtil mSpeechRecognitionUtil;
    public void initSpeechRecognitionUtil(){
        //初始话语音识别类，还要传入监听类
        mSpeechRecognitionUtil = SpeechRecognitionUtil.getInstance(translateActivity, new MySpeechRecognitionListener());
    }


    /**
     * 开始语音识别
     * @param outFilePath
     * @param recognitionType
     */
    public void startSpeech(String outFilePath, int recognitionType){
        if(mSpeechRecognitionUtil != null){
            mSpeechRecognitionUtil.startSpeech(outFilePath, recognitionType);
        }
    }

    /**
     * 停止语音识别
     */
    public void stopRecognize(){
        if(mSpeechRecognitionUtil != null){
            mSpeechRecognitionUtil.stop();
        }
    }

    /**
     * 语音识别监听
     */
    class MySpeechRecognitionListener implements EventListener {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            /*
            String result = "name：" + name;
            if (length > 0 && data.length > 0) {
                result += ", 语义解析结果：" + new String(data, offset, length);
                //Log.d(TAG, "识别到了："+ result);
            }
            */
            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                Log.i(TAG, "准备开始说话：");
                //回调给界面，开始进度条和改变按钮图片表示开始说话
                if(mListener != null){
                    mListener.startSpeech();
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                Log.i(TAG, "检测到开始说话：");

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户的已经停止说话
                Log.i(TAG, "检测到已经停止说话了：");
                if(mListener != null){
                    mListener.endSpeech();
                }
            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
                //不断接收的结果，包括临时、最后结果、语义理解结果

                Log.i(TAG, "onEvent: 语音识别参数：" + params);
                //根据参数获取返回类型
                String resultType = SpeechRecognitionUtil.getResultType(params);
                //得到最后结果时
                if (SpeechRecognitionUtil.VALUE_FINAL_RESULT.equals(resultType)) {
                    String finalResult = SpeechRecognitionUtil.getRealResult(params);
                    Log.i(TAG, "onEvent: 最后的识别结果：" + finalResult);
                    if (mListener != null) {
                        mListener.finalRecoginze(finalResult);
                    }
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                // 识别结束， 最终识别结果或可能的错误
                ///////////***********这里有问题！！！！！！********///////////////////////////////
                Log.i(TAG, "onEvent: 识别结束了"+params);
                //结束了就告诉界面工具
                if(mListener != null){
                    mListener.wellRecognize();
                }
            }
        }
    }





    /***************以下为翻译*****************************************/

    /**
     * 初始化翻译类
     */
    private TranslateManager mTranslateManager;
    private void initTranslateManager(){
        mTranslateManager = new TranslateManager();
        //设置翻译监听
        mTranslateManager.setTranslationListener(new MyTranslationListener());
    }

    //翻译方式常量
    public static final int TRANSLATEWAY_C_TO_E = TranslateManager.C_TO_E;
    public static final int TRANSLATEWAY_E_TO_C = TranslateManager.E_TO_C;

    /**
     * 对String进行翻译
     * @param content
     * @param translateWay
     */
    public void translate(String content, int translateWay){
        if(mTranslateManager != null){
            mTranslateManager.translate(content , translateWay);
        }
    }

    //翻译监听
    class MyTranslationListener implements TranslateManager.TranslationListener {
        @Override
        public void wellTranslation(String realResult) {
            //传回界面工具翻译结果
            if(mListener != null){
                mListener.wellTranslate(realResult);
            }
        }
    }





    /***************以下为语音合成*****************************************/

    /**
     * 初始化语音合成类
     */
    private SpeechSynthesisUtil speechSynthesisUtil;
    public void initSpeechSynthesisUtil(){
        speechSynthesisUtil = SpeechSynthesisUtil.getInstance(translateActivity);
        Map<String, String> map = DeployMessageUtil.getSpeechSynthesisMessage();
        String speechSpeaker = map.get(DeployMessageUtil.SynthesisSpeaker);
        String speechVolume = map.get(DeployMessageUtil.SynthesisVolume);
        String speechSpeed = map.get(DeployMessageUtil.SynthesisSpeed);
        String speechPitch = map.get(DeployMessageUtil.SynthesisPitch);
        Log.i(TAG, "initSpeechSynthesisUtil: 从属性文件取出信息"
                + speechSpeaker + speechVolume + speechSpeed + speechPitch);
        if (speechSynthesisUtil != null && speechSpeaker != null){
            speechSynthesisUtil.init(speechSpeaker, speechVolume, speechSpeed,speechPitch);
            speechSynthesisUtil.setSpeechSynthesizerListener(new MySpeechSynthesizerListener());
        } else{
            Log.e(TAG, "initSpeechSynthesisUtil: 初始化语音合成出错，属性文件能否取出："
                    + (speechSpeaker != null));
        }
    }

    /**
     * 自定义语音合成监听类
     */
    class  MySpeechSynthesizerListener implements SpeechSynthesizerListener {
        //开始合成
        @Override
        public void onSynthesizeStart(String s) {
            Log.i(TAG, "onSynthesizeStart: 开始合成");
        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

        }
        //合成结束
        @Override
        public void onSynthesizeFinish(String s) {
            Log.i(TAG, "onSynthesizeFinish: 合成结束");
        }
        //开始播放
        @Override
        public void onSpeechStart(String s) {
            Log.i(TAG, "onSpeechStart: 开始播放");
        }
        //播放中
        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }
        //播放完成
        @Override
        public void onSpeechFinish(String s) {
            Log.i(TAG, "onSpeechFinish: 播放完成");
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            Log.e(TAG, "onError: 错误了啊"+speechError.toString());
        }
    }

    /**
     * 开始语音合成
     * @param content
     */
    public void speak(String content){
        if(speechSynthesisUtil != null) speechSynthesisUtil.speak(content);
    }

}

package com.example.activity.memo;

import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.example.util.DeployMessageUtil;
import com.example.util.PropertiesUtil;
import com.example.util.SpeechRecognitionUtil;
import com.example.util.SpeechSynthesisUtil;
import com.example.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BackgroundUtil {
    private static final String TAG = "BackgroundUtil";
    private MemoActivity memoActivity;

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
    }
    //回调监听
    private BackgroundUtilListener mListener;
    public void setBackgroundUtilListener(BackgroundUtilListener mListener){
        this.mListener = mListener;
    }

    //单例
    public static BackgroundUtil instance;
    public static synchronized BackgroundUtil getInstance(MemoActivity memoActivity){
        if(instance == null)instance = new BackgroundUtil(memoActivity);
        return instance;
    }
    //私有构造器
    private BackgroundUtil(MemoActivity memoActivity){
        this.memoActivity = memoActivity;
        //初始化所有需要用的东西
        init();
    }

    private void  init(){
        //首先应该加载数据库,并创建Memo表
        loadDataBase();
        //初始化语音识别工具
        initSpeechRecognitionUtil();
        //初始化语音合成工具
        initSpeechSynthesisUtil();

    }

    /***************以下为数据库操作*****************************************/

    /**
     * 加载数据库,并创建Memo表
     */
    private MemoDBHelper memoDBHelper = null;
    private void loadDataBase(){
        memoDBHelper = new MemoDBHelper(memoActivity);
    }

    /**
     * 添加一条数据
     * 传入一段文本，识别时间然后插入数据库
     * @param str
     * @return
     */
    public Message insert(String str) {
        //直接使用TimeUtil工具类得到识别时间（没识别到就是得到当前时间）
        Long timeLong = TimeUtil.getTime(str);

        //把Long转化为yyyy-MM-dd格式字符串
        Date tempDate = new Date(timeLong);
        DateFormat tempDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = tempDateFormat.format(tempDate);

        //新建一个对象用于传值
        Message insertMessage = new Message(1l, timeStr, str);
        return memoDBHelper.insert(insertMessage);
    }

    /**
     * 删除一条数据
     * @param message
     * @return
     */
    public boolean delete(Message message) {
        return memoDBHelper.delete(message);
    }

    /**
     * 修改一条数据
     * @param message
     * @return
     */
    public boolean update(Message message) {
        return memoDBHelper.update(message);
    }

    /**
     * 按时间升序查询所有数据
     * @return
     */
    public List<Message> selectAll() {
        return memoDBHelper.selectAll();
    }

    public int selectById(long id) {
        return memoDBHelper.selectById(id);
    }





    /***************以下为语音识别*****************************************/

    //识别方式常量
    public static final int SPEECH_CHINESE = SpeechRecognitionUtil.RECOGNITION_TYPE_UNDERSTAND;
    public static final int SPEECH_ENGLISH = SpeechRecognitionUtil.RECOGNITION_TYPE_ENGLISH;

    /**
     * 初始化语音识别工具类
     */
    private SpeechRecognitionUtil speechRecognitionUtil;
    public void initSpeechRecognitionUtil(){
        speechRecognitionUtil = SpeechRecognitionUtil.getInstance(memoActivity, new MySpeechRecognitionListener());
    }

    /**
     * 开始语音识别
     */
    public void startRecognize(int RecognitionType){
        if (speechRecognitionUtil != null) {
            speechRecognitionUtil.startSpeech(null, RecognitionType);
        } else {
            Log.e(TAG, "startSpeechRecognition: 开始语音识别出错，工具类为null");
        }
    }

    /**
     * 停止语音识别
     */
    public void stopRecognize(){
        if(speechRecognitionUtil != null){
            speechRecognitionUtil.stop();
        }
    }

    /**
     * 语音识别监听类
     */
    class MySpeechRecognitionListener implements EventListener {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {

            /*
            String result = "name：" + name;
            if (length > 0 && data.length > 0) {
                 result += ", 语义解析结果：" + new String(data, offset, length);
                 Log.i(TAG, "识别到了："+ result);
            }
            */

            if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                // 引擎准备就绪，可以开始说话
                Log.i(TAG, "onEvent: 准备开始说话：");
                //回调给界面，开始进度条和改变按钮图片表示开始说话
                if(mListener != null){
                    mListener.startSpeech();
                }

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_BEGIN)) {
                // 检测到用户的已经开始说话
                Log.i(TAG, "onEvent: 检测到开始说话：");

            } else if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_END)) {
                // 检测到用户的已经停止说话
                Log.i(TAG, "onEvent: 检测到已经停止说话了：");
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

                Log.i(TAG, "onEvent: 识别结束了"+params);
                //结束了就告诉界面工具
                if(mListener != null){
                    mListener.wellRecognize();
                }
            }
        }
    }





    /***************以下为语音合成*****************************************/

    /**
     * 初始化语音合成工具类
     */
    private SpeechSynthesisUtil speechSynthesisUtil;
    public void initSpeechSynthesisUtil(){
        speechSynthesisUtil = SpeechSynthesisUtil.getInstance(memoActivity);
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
     * @param str
     */
    public void speak(String str){
        if(speechSynthesisUtil != null) {
            speechSynthesisUtil.speak(str);
        } else {
            Log.e(TAG, "speak: 语音合成出错，工具类为null");
        }
    }


}

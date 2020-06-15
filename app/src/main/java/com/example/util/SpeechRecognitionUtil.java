package com.example.util;

import android.content.Context;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpeechRecognitionUtil {
    private static final String TAG = "SpeechRecognitionUtil";

    public static final int RECOGNITION_TYPE_CHINESE = 15372;//中文
    public static final int RECOGNITION_TYPE_ENGLISH = 17372;//英文
    public static final int RECOGNITION_TYPE_CANTONESE = 16372;//粤语
    public static final int RECOGNITION_TYPE_UNDERSTAND = 15373;//中文语义理解

    //识别的功能类
    private EventManager asr;
    //识别参数集合
    private Map<String, Object> params;
    //监听识别的监听器
    private EventListener e;

    //构造器，传入Context和监听器
    private SpeechRecognitionUtil(Context context, EventListener e){
        init(context,e);
    }
    //单例
    public static SpeechRecognitionUtil instance;
    //获取单例，必须传入Context和
    public static synchronized SpeechRecognitionUtil getInstance(Context context, EventListener e){
        if (instance == null) instance = new SpeechRecognitionUtil(context, e);
        return instance;
    }

    //初始化EventManager管理器
    private void init(Context context,EventListener e){
        asr = EventManagerFactory.create(context, "asr");
        this.e = e;
        asr.registerListener(e);
        params = new LinkedHashMap<String, Object>();


        //自己的
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT,1200);//不开启长语音（1.5s则为超时，表示停止说话）
        //params.put(SpeechConstant.VAD, SpeechConstant.VAD_TOUCH);//不开启长语音时，超时则关闭语音活动检测，要手动关闭语音识别
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);//不需要音量回调
        params.put(SpeechConstant.ACCEPT_AUDIO_DATA, true);//需要音频数据回调
        params.put(SpeechConstant.DISABLE_PUNCTUATION,false);//不禁用标点符号

    }

    public void startSpeech(String outFilePath, int recognitionType){
        //识别输出到的文件
        if(outFilePath != null && outFilePath.trim().length() != 0){
            params.put(SpeechConstant.OUT_FILE, outFilePath);
        }
        //识别类型
        params.put(SpeechConstant.PID, recognitionType);

        //把参数都封装成json数据
        String json = null;
        json = new JSONObject(params).toString();
        //开始识别
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    //发送停止录音事件，提前结束录音等待识别结果
    public void stop() {
        if(asr != null){
            asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        }
    }

    //取消本次识别，取消后将立即停止不会返回识别结果
    public void cancel(){
        if(asr != null){
            asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        }

    }

    //始放资源
    public void release(){
        //先取消
        cancel();
        //再始放资源
        if(asr != null){
            asr.unregisterListener(e);
        }
        //单例为空
        instance = null;
    }

    //从json数据解析识别结果
    //json中识别结果的键
    private final static String key_recognizeResult = "results_recognition";
    public static String getRealResult(String str){
        //解析返回的JSON数据
        JSONObject jsonObject = null;
        String realResult = null;
        if(str != null && !str.isEmpty()){
            try {
                jsonObject = new JSONObject(str);
                //通过key（text）获取value，即获得机器人回复的结果
                realResult = jsonObject.getString(key_recognizeResult);
                //还要再修改以下才是纯文字
                if(realResult != null && !realResult.isEmpty()){
                    realResult = realResult.substring(2 , realResult.length() - 2);
                }

            } catch (JSONException e) {
                //e.printStackTrace();
                Log.e(TAG, "getRealResult：解析识别结果json数据有问题");
            }finally {
                //有没问题都要返回，空结果也返回
                return realResult;
            }
        }
        return realResult;
    }

    //json数据的返回类型的key值
    private final static String key_resultType = "result_type";
    //json数据的返回类型的value值---最后结果
    public final static String VALUE_FINAL_RESULT = "final_result";
    //json数据的返回类型的value值---语义结果
    public final static String VALUE_UNDERSTAND_RESULT = "nlu_result";

    /**
     * 获取result_type，参数的返回类型
     * @param str
     * @return
     */
    public static String getResultType(String str) {
        //解析返回的JSON数据
        JSONObject jsonObject = null;
        String resultType = null;
        if(str != null && !str.isEmpty()){
            try {
                jsonObject = new JSONObject(str);
                //通过key（text）获取value，即获得机器人回复的结果
                resultType = jsonObject.getString(key_resultType);
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.i(TAG, "getResultType: 解析识别结果json数据有问题");
            }finally {
                //有没问题都要返回，空结果也返回
                return resultType;
            }
        }
        return resultType;
    }


    //语义理解参数中的domain外括号的大key
    private final static String key_results = "results";
    //语义理解参数中的domain的真正key
    private final static String key_domain = "domain";

    /**
     * 获取语义理解结果
     * @param str
     * @return
     */
    public static String getDomain(String str){
        //解析返回的JSON数据
        JSONObject jsonObject = null;
        String results = null;
        String domain = null;
        if(str != null && !str.isEmpty()){
            try {
                jsonObject = new JSONObject(str);
                //通过key（text）获取value，即获得机器人回复的结果
                results = jsonObject.getString(key_results);
                //去掉框框再去获取
                if (results != null && !"".equals(results)) {
                    results = results.substring(1 , results.length() - 1);
                    if (results == null || "".equals(results)) return domain;
                    jsonObject = new JSONObject(results);
                    domain = jsonObject.getString(key_domain);
                    return domain;
                }
                return domain;
            } catch (JSONException e) {
                //e.printStackTrace();
                Log.i(TAG, "getDomain: 解析domain有问题");
            }finally {
                //有没问题都要返回，空结果也返回
                return domain;
            }
        }
        return domain;
    }
    /*
    private void handleResult(String result) {
        try {
            JSONObject r = new JSONObject(result);
            result = r.optString("json_res");
            r = new JSONObject(result);
            String query = r.optString("raw_text");
            if (!TextUtils.isEmpty(query)) {
                mAdapter.add(new ChatItem(query));
                mAdapter.notifyDataSetChanged();
            }
            JSONArray commands = r.optJSONArray("results");
            JSONObject command = null;
            String type = null;
            if (commands != null && commands.length() > 0) {
                command = commands.optJSONObject(0);
                type = command.optString("domain");
            } else {
                commands = r.optJSONArray("commandlist");
                if (commands != null && commands.length() > 0) {
                    command = commands.optJSONObject(0);
                    type = command.optString("commandtype");

                }
            }
            if (!TextUtils.isEmpty(type)) {
                Log.i("TYPE", type);
                new CommandProcessorTask().execute(mProcessors.getProcessor(type), command);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
    */
}

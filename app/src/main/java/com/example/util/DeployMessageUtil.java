package com.example.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.application.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置信息工具类
 * 参考：https://www.jianshu.com/p/d2a12f531d97
 *      https://juejin.im/post/5adc444df265da0b886d00bc
 */
public class DeployMessageUtil {

    //初始信息文件名：
    private static String initMessageName = "initMessage";

    //语音合成的发音人，音量，语速，音调
    public static final String SynthesisSpeaker= "SpeechSynthesis.speechSpeaker";
    public static final String SynthesisVolume= "SpeechSynthesis.speechVolume";
    public static final String SynthesisSpeed= "SpeechSynthesis.speechSpeed";
    public static final String SynthesisPitch= "SpeechSynthesis.speechPitch";

    /**
     * 初始化语音合成信息（没有才配置，配置的都是默认值）
     */
    public static void initSpeechSynthesisMessage(){
        //创建对象
        SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences(initMessageName, Context.MODE_PRIVATE);
        //编辑对象
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //配置发音人，如果没有配置则配置
        if (sharedPreferences.getString(SynthesisSpeaker, null) == null) {
            editor.putString(SynthesisSpeaker, "0");
        }
        //配置音量，如果没有配置则配置
        if (sharedPreferences.getString(SynthesisVolume, null) == null) {
            editor.putString(SynthesisVolume, "5");
        }
        //配置语速，如果没有配置则配置
        if (sharedPreferences.getString(SynthesisSpeed, null) == null) {
            editor.putString(SynthesisSpeed, "5");
        }
        //配置语速，如果没有配置则配置
        if (sharedPreferences.getString(SynthesisPitch, null) == null) {
            editor.putString(SynthesisPitch, "5");
        }
        //提交
        editor.apply();
    }

    /**
     * 得到语音合成的配置信息
     * @return
     */
    public static Map<String, String> getSpeechSynthesisMessage() {
        Map<String ,String> map = new HashMap<>();
        //创建对象
        SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences(initMessageName, Context.MODE_PRIVATE);
        map.put(SynthesisSpeaker, sharedPreferences.getString(SynthesisSpeaker, "0"));
        map.put(SynthesisVolume, sharedPreferences.getString(SynthesisVolume, "5"));
        map.put(SynthesisSpeed, sharedPreferences.getString(SynthesisSpeed, "5"));
        map.put(SynthesisPitch, sharedPreferences.getString(SynthesisPitch, "5"));
        return map;
    }

    /**
     * 修改语音合成的配置信息
     * @param map
     */
    public static void setSpeechSynthesisMessage(Map<String, String> map) {
        //创建对象
        SharedPreferences sharedPreferences = MyApplication.context.getSharedPreferences(initMessageName, Context.MODE_PRIVATE);
        //编辑对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //设置值
        editor.putString(SynthesisSpeaker, map.get(SynthesisSpeaker));
        editor.putString(SynthesisVolume, map.get(SynthesisVolume));
        editor.putString(SynthesisSpeed, map.get(SynthesisSpeed));
        editor.putString(SynthesisPitch, map.get(SynthesisPitch));
        //记得提交
        editor.apply();
    }
}

package com.example.activity.set;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.example.util.DeployMessageUtil;
import com.example.util.SpeechSynthesisUtil;
import com.example.voice.R;

import java.util.Map;

/**
 * 界面工具类
 * 问题：SeekBar和TextView初始化时必须先把两个控件find到再设置监听，
 *      否则先SeekBar设置监听，然后初始化时找不到TextView则出错
 */
public class InterfaceUtil{
    private static final String TAG = "InterfaceUtil";
    private  SpeechSetActivity speechSetActivity;

    //单例
    public static InterfaceUtil instance;
    public static synchronized InterfaceUtil getInstance(SpeechSetActivity speechSetActivity){
        if(instance == null)instance = new InterfaceUtil(speechSetActivity);
        return instance;
    }
    //私有构造器
    private InterfaceUtil(SpeechSetActivity speechSetActivity){
        this.speechSetActivity = speechSetActivity;
        //初始化所有需要用的东西
        init();
    }

    /**
     * 初始化所有东西
     */
    private void init(){
        //初始化配置信息集合
        initMap();
        //初始化语音合成工具类
        initSpeechSynthesisUtil();
        //初始化单选按钮组
        initRadioGruop();
        //初始化各种Text和SeekBar
        initTextViewAndSeekBar();
        //初始化播放按钮
        initSpeakButton();
    }

    /***************以下为配置信息*****************************************/

    //语音合成配置信息集合
    private Map<String, String> map;

    /**
     * 初始化语音合成配置信息
     */
    private void initMap() {
        map = DeployMessageUtil.getSpeechSynthesisMessage();
    }

    /**
     * 保存语音合成配置信息
     */
    public void saveSpeechSynthesisMessage() {
        DeployMessageUtil.setSpeechSynthesisMessage(map);
    }






    /***************以下为语音合成*****************************************/

    private final String speakStr= "请选择所需要播放音频的发音人、音量、语速以及音调";

    /**
     * 初始化语音合成类
     */
    private SpeechSynthesisUtil speechSynthesisUtil;
    public void initSpeechSynthesisUtil(){
        speechSynthesisUtil = SpeechSynthesisUtil.getInstance(speechSetActivity);
        //配置信息工具类去获取语音合成配置信息集合
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
            isSpeaking = true;
            //UI线程设置图标
            Message message = Message.obtain();
            message.what = START_SPEAK;
            mHandler.sendMessage(message);
        }
        //播放中
        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }
        //播放完成
        @Override
        public void onSpeechFinish(String s) {
            Log.i(TAG, "onSpeechFinish: 播放完成");
            //播放结束所有东西再初始化
            isPress = false;
            isSpeaking = false;
            //UI线程设置图标
            Message message = Message.obtain();
            message.what = FINISH_SPEAK;
            mHandler.sendMessage(message);
        }

        @Override
        public void onError(String s, SpeechError speechError) {
            Log.e(TAG, "onError: 错误了啊"+speechError.toString());
            //错误了就所有复位
            isPress = false;
            isSpeaking = false;
            speechSynthesisUtil.stop();
            //UI线程设置图标
            Message message = Message.obtain();
            message.what = FINISH_SPEAK;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 开始语音合成
     * @param content
     */
    public void speak(String content){
        if(speechSynthesisUtil != null) speechSynthesisUtil.speak(content);
    }

    /**
     * 结束播放
     */
    public void stopSpeak() {
        if (speechSynthesisUtil != null) speechSynthesisUtil.stop();
    }





    /***************以下为单选按钮*****************************************/

    //单选按钮组
    private RadioGroup radioGruop;

    private void initRadioGruop() {
        if (speechSetActivity != null) {
            //单选按钮组
            radioGruop = speechSetActivity.findViewById(R.id.speechset_activity_radiogruop);
            radioGruop.setOnCheckedChangeListener(new MyRadioGruopListener());
            //设置初始值
            switch (map.get(DeployMessageUtil.SynthesisSpeaker)) {
                case "0":
                    if (radioGruop != null) radioGruop.check(R.id.speechset_activity_radiobutton0);
                    break;
                case "1":
                    if (radioGruop != null) radioGruop.check(R.id.speechset_activity_radiobutton1);
                    break;
                case "2":
                    if (radioGruop != null) radioGruop.check(R.id.speechset_activity_radiobutton2);
                    break;
                case "3":
                    if (radioGruop != null) radioGruop.check(R.id.speechset_activity_radiobutton3);
                    break;
                case "4":
                    if (radioGruop != null) radioGruop.check(R.id.speechset_activity_radiobutton4);
                    break;
            }
        }
    }

    /**
     * 自定义RadioGroup监听
     * 每次单选改变就重设map中Speak的值
     */
    class MyRadioGruopListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.speechset_activity_radiobutton0:
                    map.put(DeployMessageUtil.SynthesisSpeaker, "0");
                    break;
                case R.id.speechset_activity_radiobutton1:
                    map.put(DeployMessageUtil.SynthesisSpeaker, "1");
                    break;
                case R.id.speechset_activity_radiobutton2:
                    map.put(DeployMessageUtil.SynthesisSpeaker, "2");
                    break;
                case R.id.speechset_activity_radiobutton3:
                    map.put(DeployMessageUtil.SynthesisSpeaker, "3");
                    break;
                case R.id.speechset_activity_radiobutton4:
                    map.put(DeployMessageUtil.SynthesisSpeaker, "4");
                    break;
            }
        }
    }






    /***************以下为单选按钮*****************************************/

    //音量
    private SeekBar volumeSeekBar;
    private TextView volumeTextView;
    private String volumeStr;
    //语速
    private SeekBar speedSeekBar;
    private TextView speedTextView;
    private String speedStr;
    //音调
    private SeekBar pitchSeekBar;
    private TextView pitchTextView;
    private String pitchStr;

    /**
     * 初始化各种TextView和SeekBar
     * 获取字符串，如“音量：”
     * 设置监听，设置初始值
     */
    private void initTextViewAndSeekBar() {
        //自定义SeekBar监听
        MySeekBarListener mySeekBarListener = new MySeekBarListener();

        if (speechSetActivity != null) {
            //音量SeekBar和TextView
            volumeStr = speechSetActivity.getResources().getString(R.string.str_speechset_volume);
            volumeSeekBar = speechSetActivity.findViewById(R.id.speechset_activity_volumeseekbar);
            volumeTextView = speechSetActivity.findViewById(R.id.speechset_activity_volumetextview);
            volumeSeekBar.setOnSeekBarChangeListener(mySeekBarListener);
            volumeSeekBar.setProgress(Integer.parseInt(map.get(DeployMessageUtil.SynthesisVolume)));
            volumeTextView.setText(volumeStr + map.get(DeployMessageUtil.SynthesisVolume));

            //语速SeekBar和TextView
            speedStr = speechSetActivity.getResources().getString(R.string.str_speechset_speed);
            speedSeekBar = speechSetActivity.findViewById(R.id.speechset_activity_speedseekbar);
            speedTextView = speechSetActivity.findViewById(R.id.speechset_activity_speedtextview);
            speedSeekBar.setOnSeekBarChangeListener(mySeekBarListener);
            speedSeekBar.setProgress(Integer.parseInt(map.get(DeployMessageUtil.SynthesisSpeed)));
            speedTextView.setText(speedStr + map.get(DeployMessageUtil.SynthesisSpeed));

            //音调SeekBar和TextView
            pitchStr = speechSetActivity.getResources().getString(R.string.str_speechset_pitch);
            pitchSeekBar = speechSetActivity.findViewById(R.id.speechset_activity_pitchseekbar);
            pitchTextView = speechSetActivity.findViewById(R.id.speechset_activity_pitchtextview);
            pitchSeekBar.setOnSeekBarChangeListener(mySeekBarListener);
            pitchSeekBar.setProgress(Integer.parseInt(map.get(DeployMessageUtil.SynthesisPitch)));
            pitchTextView.setText(pitchStr + map.get(DeployMessageUtil.SynthesisPitch));
        }
    }

    /**
     * 自定义SeekBar监听
     * 变化SeekBar就重设map中的值
     */
    class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 进度条改变时调用
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()){
                //音量
                case R.id.speechset_activity_volumeseekbar:
                    volumeTextView.setText(volumeStr + progress);
                    //更改map
                    map.put(DeployMessageUtil.SynthesisVolume, progress + "");
                    break;
                case R.id.speechset_activity_speedseekbar:
                    speedTextView.setText(speedStr + progress);
                    //更改map
                    map.put(DeployMessageUtil.SynthesisSpeed, progress + "");
                    break;
                case R.id.speechset_activity_pitchseekbar:
                    pitchTextView.setText(pitchStr + progress);
                    //更改map
                    map.put(DeployMessageUtil.SynthesisPitch, progress + "");
                    break;
            }
        }

        /**
         * 触碰到进度条时调用
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 不再触碰进度条时调用
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }





    /***************以下为单选按钮*****************************************/

    //播放按钮
    private ImageButton imageButton;
    //是否按了按钮（准备好说话才表示按了）
    private boolean isPress = false;
    //是否正在播放语音合成，默认没有
    private boolean isSpeaking = false;

    private void initSpeakButton() {
        if (speechSetActivity != null) {
            imageButton = speechSetActivity.findViewById(R.id.speechset_activity_speakbutton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pressButton();
                }
            });
        }
    }

    /**
     * 按下按钮触发的方法
     */
    public void pressButton() {
        //如果没有按下则正常语音合成
        if (!isPress) {
            //重设语音参数
            speechSynthesisUtil.reSetSpeakParam(map);
            isPress = true;
            speak(speakStr);
        } else {
            //正在播放才能stop
            if (isSpeaking) {
                stopSpeak();
                //播放结束所有东西再初始化
                isPress = false;
                isSpeaking = false;
                imageButton.setImageResource(R.mipmap.speechset_activity_speakdefault);
            }
        }
    }





    /***************以下为其他方法*****************************************/

    //翻译结果传入Bundle的key
    private static final String translateResultKey = "translateResult";

    //信息常量
    private static final int START_SPEAK = 0X114;        //开始播放
    private static final int FINISH_SPEAK = 0X115;        //结束播放

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
                //开始播放
                case START_SPEAK:
                    imageButton.setImageResource(R.mipmap.speechset_activity_speakpress);
                    break;
                //结束播放
                case FINISH_SPEAK:
                    imageButton.setImageResource(R.mipmap.speechset_activity_speakdefault);
                    break;
            }
        };
    };
}

package com.example.activity.memo;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.activity.updatememo.UpdateMemoActivity;
import com.example.voice.R;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InterfaceUtil {
    private static final String TAG = "InterfaceUtil";

    private MemoActivity memoActivity;

    //单例
    public static InterfaceUtil instance;
    public static synchronized InterfaceUtil getInstance(MemoActivity memoActivity){
        if(instance == null)instance = new InterfaceUtil(memoActivity);
        return instance;
    }
    //私有构造器
    private InterfaceUtil(MemoActivity memoActivity){
        this.memoActivity = memoActivity;
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
    }



    /*****************以下为后台工具类**********************/

    /**
     * 初始化后台工具类
     */
    public BackgroundUtil backgroundUtil;
    private void initBackgroundUtil(){
        backgroundUtil = BackgroundUtil.getInstance(memoActivity);
        backgroundUtil.setBackgroundUtilListener(new MyBackgroundUtilListener());
    }

    /**
     * 后台工具类监听
     */
    class MyBackgroundUtilListener implements BackgroundUtil.BackgroundUtilListener{

        //最后结果
        private String finalResult = null;

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
                //识别到结果了，就让后台工具类去识别并且存入数据库
                handleFinalResult();
            }else{
                //提示用户没有说话
                Toast.makeText(memoActivity,"检测到用户没有说话",Toast.LENGTH_SHORT).show();
            }
            //流程结束了
            reset();
            //识别完就设置为空
            finalResult = null;
        }

        //识别结束了处理最后的结果(基本不错出错，一定会插入一条数据，有错误也是插入当前时间的数据)
        private void handleFinalResult() {
            //去插入这个识别结果
            Message insertMessage = backgroundUtil.insert(finalResult);
            long resultId = insertMessage.getId();
            //插入成功则播报添加成功
            if (resultId > 0) {
                //成功了就刷新界面啊
                //查询新数据排序后的下标
                int index = backgroundUtil.selectById(resultId);
                //刷新
                notifyItemInserted(index, insertMessage);
                //语音合成播放，添加成功
                backgroundUtil.speak("添加成功");
            } else {
                //语音合成播放，添加失败
                backgroundUtil.speak("添加失败");
            }

        }
    }





    /*****************以下为List控件**********************/

    //列表控件
    private RecyclerView recyclerView;
    //列表的适配器
    private MessageAdapter messageAdapter;
    //适配器的信息集合
    private List<Message> list = new ArrayList<Message>();

    /**
     * 初始化列表
     */
    private void initRecyclerView(){
        recyclerView = (RecyclerView)memoActivity.findViewById(R.id.memo_activity_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(memoActivity);
        if (recyclerView != null) recyclerView.setLayoutManager(mLayoutManager);
        messageAdapter = new MessageAdapter(list ,memoActivity);
        if (recyclerView != null) recyclerView.setAdapter(messageAdapter);

        //查询所有数据并显示
        selectAllList();

    }

    /**
     * 关闭某个菜单，菜单对象是从适配器传过来的
     * @param lastSlidingMenu
     */
    public void closeLastMenu(SlidingMenu lastSlidingMenu) {
        if (lastSlidingMenu!= null && lastSlidingMenu.isOpen()) {
            Log.e(TAG, "closeOpenMenu: 关闭最后一个打开的菜单");
            lastSlidingMenu.closeMenu();
        }
    }

    /**
     * 查询所有数据并且刷新界面
     */
    public void selectAllList() {
        //select所有数据并且直接把返回的集合设置为适配器的List
        list = backgroundUtil.selectAll();
        messageAdapter.setmList(list);
        //通知所有数据发生了变化
        messageAdapter.notifyDataSetChanged();
        //显示到最后一行
        //recyclerView.scrollToPosition(list.size() - 1);
    }

    /**
     * 根据下标刷新List（有动画）
     * @param index
     * @param message
     */
    private void notifyItemInserted(int index, Message message) {
        list.add(index, message);
        //刷新滚动控件
        messageAdapter.notifyItemInserted(index);
    }

    /**
     * 删除一条信息,先刷新界面，再移除对象
     * @param index
     */
    private void notifyItemMoved(int index) {
        messageAdapter.notifyItemRemoved(index);
        list.remove(index);

    }

    /**
     * 删除一条数据
     * @param id
     */
    public void deleteMessage(long id) {
        int index = 0;
        for (Message message : list) {
            if (message.getId() == id) break;
            index++;
        }
        //根据list集合的index下标的Message删除数据，删除成功再刷新列表
        if (backgroundUtil.delete(list.get(index))) {
            //list控件移除这一条
            notifyItemMoved(index);
            //刷新了以后，就没有最后一个打开了的菜单了，因为已经删除了
            messageAdapter.setLastMenu(null);
        } else {
            Log.e(TAG, "deleteMessage: 无法删除");
            Toast.makeText(memoActivity,"删除信息失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据id打开这个Item
     * @param id
     */
    public void openItem(long id) {
        Message updateMessage = null;
        for (Message message : list) {
            if (message.getId() == id) {
                updateMessage = message;
                break;
            }
        }
        //找到以后直接打开
        UpdateMemoActivity.actionStart(memoActivity, updateMessage);
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
        if(memoActivity != null){
            speechButton = memoActivity.findViewById(R.id.memo_activity_speechbutton);
            if(speechButton != null){
                speechButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: 点击了啊");
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
    public void pressButton() {
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
        if(memoActivity != null){
            mkLoader = memoActivity.findViewById(R.id.memo_activity_mkloader);
        }
    }

    /**
     * 显示加载控件
     */
    private void showMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.INVISIBLE);
            mkLoader.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏加载控件
     */
    private void hideMKLoader(){
        if(speechButton != null && mkLoader != null){
            speechButton.setVisibility(View.VISIBLE);
            mkLoader.setVisibility(View.INVISIBLE);
        }
    }





    /*****************以下为其他方法**********************/

    //UI是否开始了
    private boolean isUIStart = false;

    /**
     * 开始变化UI
     */
    private void startUI(){
        //开始UI一定正在录音了
        isRecognizing = true;
        //开始进度条和换图片(UI没开始再开始好吗）
        if(!isUIStart){
            isUIStart = true;
            if (speechButton != null) speechButton.setImageResource(R.mipmap.chat_activity_audio1);
            if (speechButton != null) speechButton.setBackground(memoActivity.getResources().getDrawable(R.drawable.memo_activity_speechbutton_backgroundpress));
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
        if(isUIStart){
            isUIStart = false;
            if (speechButton != null) speechButton.setImageResource(R.mipmap.memo_activity_add_128);
            if (speechButton != null) speechButton.setBackground(memoActivity.getResources().getDrawable(R.drawable.memo_activity_speechbutton_backgrounddefault));
        }
    }

    /**
     * 重置一些东西
     */
    private void reset(){
        isWorking = false;
    }



}

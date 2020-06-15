package com.example.activity.updatememo;

import android.view.View;
import android.widget.TextView;

import com.example.activity.memo.MemoDBHelper;
import com.example.activity.memo.Message;
import com.example.voice.R;

public class InterfaceUtil {
    private static final String TAG = "InterfaceUtil";

    private UpdateMemoActivity updateMemoActivity;

    private Message message;

    //单例
    public static InterfaceUtil instance;
    public static synchronized InterfaceUtil getInstance(UpdateMemoActivity updateMemoActivity, Message message){
        if(instance == null)instance = new InterfaceUtil(updateMemoActivity, message);
        return instance;
    }
    //私有构造器
    private InterfaceUtil(UpdateMemoActivity updateMemoActivity, Message message){
        this.updateMemoActivity = updateMemoActivity;
        this.message = message;
        //初始化所有需要用的东西
        init();
    }

    private void init(){
        //初始化数据库工具类
        initMemoDBHelper();
        //初始化界面控件
        initComponent();
    }

    //数据库工具类
    private MemoDBHelper memoDBHelper = null;

    /**
     * 初始化数据库工具类
     */
    private void initMemoDBHelper() {
        memoDBHelper = new MemoDBHelper(updateMemoActivity);
    }

    /**
     * 初始化所有控件
     */
    private void initComponent(){
        //
        initTimeTextView();
        //初始化内容的TextView控件
        initContentTextView();

    }

    //时间的TextView
    private TextView timeTextView;

    /**
     * 初始化时间的TextView控件
     */
    private void initTimeTextView() {
        if (updateMemoActivity != null && message != null) {
            timeTextView = updateMemoActivity.findViewById(R.id.updatememo_activity_time);
            timeTextView.setText(message.getTime());
        }
    }


    //内容的TextView
    private TextView contentTextView;

    /**
     * 初始化内容的TextView控件
     */
    private void initContentTextView() {
        if (updateMemoActivity != null && message != null) {
            contentTextView = updateMemoActivity.findViewById(R.id.updatememo_activity_content);
            contentTextView.setText(message.getContent());
        }
    }

    /**
     * 自动保存信息
     * 如果content有内容则更新数据
     * 如果没有则什么都不干，不能自动删除，应该在MemoActivity界面删除
     */
    public void saveMessage() {
        message.setContent(contentTextView.getText().toString());
        //有内容则保存，没有则删除
        if (message.getContent() != null && !message.getContent().isEmpty()) {
            memoDBHelper.update(message);
        }
    }

}

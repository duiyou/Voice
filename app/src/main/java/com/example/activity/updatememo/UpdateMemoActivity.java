package com.example.activity.updatememo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.activity.BaseActivity;
import com.example.activity.memo.Message;
import com.example.voice.R;

public class UpdateMemoActivity extends BaseActivity {
    private static final String TAG = "UpdateMemoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatememo_activity);
        //接收传递过来的备忘对象Message
        Message message = (Message)getIntent().getSerializableExtra(intentKey);
        Log.i(TAG, "onCreate: 传递的对象id：" + message.getId());
        //初始化所有信息
        init(message);

    }

    //初始化所有信息
    private void init(Message message){
        //初始化界面工具类
        initInterfaceUtil(message);
    }

    /**
     * 初始化界面工具类
     */
    public InterfaceUtil interfaceUtil;
    private void initInterfaceUtil(Message message) {
        interfaceUtil = InterfaceUtil.getInstance(UpdateMemoActivity.this, message);
    }

    /**
     * 准备和用户交互时表示为显示
     */
    @Override
    protected void onResume() {
        super.onResume();
        //显示了
        isShow = true;
        Log.i(TAG, "onStart: 显示了啊");
    }

    /**
     * 准备启动其他活动时就保存在信息
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: 不显示了并且保存信息");
        //不可见
        isShow = false;
        interfaceUtil.saveMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (InterfaceUtil.instance != null) {
            InterfaceUtil.instance = null;
        }
        Log.e(TAG, "onDestroy: 销毁了");
        //销毁活动
        finish();
    }

    //Itent传递对象的key
    private static String intentKey = "intentKey";
    /**
     * 开始这个活动
     * 穿入一个备忘对象Message
     * Message必须实现序列化接口才能传递，因为Itent本身不能传递对象
     *      参考：https://blog.csdn.net/leejizhou/article/details/51105060
     * @param context
     */
    public static void actionStart(Context context, Message message){
        Intent intent = new Intent(context, UpdateMemoActivity.class);
        intent.putExtra(intentKey, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

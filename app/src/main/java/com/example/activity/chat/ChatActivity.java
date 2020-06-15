package com.example.activity.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.activity.BaseActivity;
import com.example.activity.memo.MemoActivity;
import com.example.activity.set.SpeechSetActivity;
import com.example.activity.translate.TranslateActivity;
import com.example.util.SpeechRecognitionUtil;
import com.example.util.SpeechSynthesisUtil;
import com.example.voice.R;

import java.lang.reflect.Method;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

/**
 * 聊天Activity
 * 活动声明周期参考：https://blog.csdn.net/winterIce1993/article/details/60954653
 * 问题1：由于所有都是单例模式，所以在onDestroy时要释放所有单例对象
 *      包括InterfaceUtil、BackgroundUtil
 * 问题2：必须重写onBackPressed方法重设back按键的效果
 *      默认back按键则销毁活动，但不想销毁活动
 */
public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //初始化所有信息
        init();

    }

    /**
     * 初始化所有信息
     */
    private void init(){
        //设置标题栏
        initToolbar();
        //初始化界面工具类
        initInterfaceUtil();
    }

    /**
     * 设置标题栏
     */
    private void initToolbar(){
        Toolbar toolbar= (Toolbar)findViewById(R.id.chat_activity_toolbar);
        setSupportActionBar(toolbar);//Toolbar 代替ActionBar
        //设置标题栏右上角图标
        toolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.chat_activity_menu_50));

    }

    /**
     * 初始化界面工具类
     */
    private InterfaceUtil interfaceUtil;
    private void initInterfaceUtil() {
        interfaceUtil = InterfaceUtil.getInstance(ChatActivity.this);
    }

    /**
     * 这个活动按下了按钮，即开始语音识别
     */
    public void pressButton() {
        interfaceUtil.pressButton();
    }

    /**
     * 加载菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_activity_menu, menu);
        return true;
    }

    /**
     * 菜单监听
     * @param menuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.chat_activity_toolbar_menu_memo:
                /*********************跳转**********************/
                MemoActivity.actionStart(this);
                break;
            case R.id.chat_activity_toolbar_menu_translate:
                TranslateActivity.actionStart(this);
                break;
            case R.id.chat_activity_toolbar_menu_set:
                SpeechSetActivity.actionStart(this);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * 加载菜单图标
     * 参考：https://blog.csdn.net/YYXXLL2/article/details/70224960
     * @param view
     * @param menu
     * @return
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.d(TAG, "onPrepareOptionsPanel: 加载图标出错");
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * 准备和用户交互时创建语音的对象类
     */
    @Override
    protected void onResume() {
        super.onResume();
        //显示了
        isShow = true;
        Log.i(TAG, "onResume: 显示");
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
        Log.i(TAG, "onPause: 消失");
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

    /**
     * 重设back按键,不销毁活动
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * 开始这个活动
     * @param context
     */
    public static void actionStart(Context context){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}

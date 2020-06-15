package com.example.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * 统一活动类，用来管理活动进出活动管理器
 */
public class BaseActivity extends AppCompatActivity {

    //是否正在显示该活动
    public boolean isShow = false;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //得到许可
        getpermissions();
        //设置强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //创建时加入活动管理器
        ActivityCollector.addActivity(this);

    }
    protected void onDestroy(){
        super.onDestroy();
        //销毁时移除活动管理器
        ActivityCollector.removeActivity(this);
    }
    //得到许可
    private void getpermissions(){
        //如果安卓版本大于23
        if (Build.VERSION.SDK_INT >= 23) {
            //请求码？？？？？
            int REQUEST_CODE_CONTACT = 101;
            //要申请的权限
            String[] permissions = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.CHANGE_NETWORK_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.INTERNET};
            //验证是否许可权限
            for (String str : permissions) {
                if (ContextCompat.checkSelfPermission(BaseActivity.this, str) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("AudioRecordButton","没权限");
                    //一次把整个permissions的权限都申请了，直接return
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
}

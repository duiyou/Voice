package com.example.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理器
 */
public class ActivityCollector{
    //活动集合
    public static List<BaseActivity> activities=new ArrayList<>();
    //添加
    public static void addActivity(BaseActivity activity){
        activities.add(activity);
    }
    //移除
    public static void removeActivity(BaseActivity activity){
        activities.remove(activity);
    }
    //销毁所有活动
    public static void FinishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}

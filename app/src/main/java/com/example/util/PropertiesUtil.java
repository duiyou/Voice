package com.example.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final String TAG = "PropertiesUtil";

    /*
    public static Properties getProperties(Context c){
        Properties urlProps;
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        urlProps = props;
        return urlProps;
    }
    */

    /**
     * 获取
     * @param context
     * @return
     */
    public static String getValue(Context context, String key) {
        String url = null;
        InputStream inputStream = null;
        Properties properties = new Properties();
        try {
            inputStream = context.getAssets().open("appConfig");
            properties.load(inputStream);
            url = properties.getProperty(key);
            /*
            inputStream = context.openFileInput("appConfig.properties");
            properties.load(inputStream);
            url = properties.getProperty(key);*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return url;
    }


    /**
     * 修改配置文件
     * @param context
     * @param keyName
     * @param keyValue
     * @return
     */
    public static void setProperties(Context context, String keyName, String keyValue) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            //inputStream = context.openFileInput("appConfig.properties");
            inputStream = context.getAssets().open("appConfig");
            Log.d(TAG, "setProperties: ????????");
            properties.load(inputStream);
            properties.setProperty(keyName, keyValue);
            outputStream = context.openFileOutput("appConfig.properties",Context.MODE_PRIVATE);
            properties.store(outputStream, null);
            outputStream.flush();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.d(TAG, "setProperties: 修改配置文件失败了");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

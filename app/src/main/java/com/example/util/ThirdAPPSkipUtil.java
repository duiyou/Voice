package com.example.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.application.MyApplication;

/**
 * 第三方APP跳转工具类
 *
 * 问题：网易云音乐无法打开
 */
public class ThirdAPPSkipUtil {
    private static final String TAG = "ThirdAPPSkipUtil";

    /**
     * 安卓的内部应用
     */
    public static final String CAMERA = "com.android.camera2";      //相机
    public static final String CAMERA_CLASSNAME = "com.android.camera.CameraLauncher";


    public static final String GALLERY = "com.android.gallery3d";   //图库
    public static final String GALLERY_CLASSNAME = "com.android.gallery3d.app.GalleryActivity";

    /**
     * description:国外热门App
     **/
    public static final String INSTAGRAM = "com.instagram.android";
    public static final String FACE_BOOK = "com.facebook.katana";
    public static final String MESSENGER = "com.facebook.orca";
    public static final String WHATS_APP = "com.whatsapp";
    public static final String GMAIL = "com.google.android.gm";
    public static final String GOOGLE_MAP = "com.google.android.apps.maps";
    public static final String ALLO = "com.google.android.apps.fireball";

    /**
     * description:国内热门App
     **/
    public static final String MEITUAN = "com.sankuai.meituan";     //美团
    public static final String MEITUAN_CLASSNAME = "com.meituan.android.pt.homepage.activity.MainActivity";


    public static final String MEITUAN_WAIMAI = "com.sankuai.meituan.takeoutnew";      //美团外卖
    public static final String E_LE_ME = "me.ele";      //饿了么
    public static final String MO_BAI = "com.mobike.mobikeapp";     //摩拜单车
    public static final String OFO = "so.ofo.labofo";
    public static final String JIN_RI_TOU_TIAO = "com.ss.android.article.news";     //今日头条
    public static final String SINA_WEI_BO = "com.sina.weibo";      //新浪微博
    public static final String WANG_YI_XIN_WEN = "com.netease.newsreader.activity";     //网易新闻
    public static final String KUAI_SHOU = "com.smile.gifmaker";    //快手


    public static final String ZHI_HU = "com.zhihu.android";        //知乎
    public static final String ZHI_HU_CLASSNAME = "com.zhihu.android.app.ui.activity.MainActivity";


    public static final String HU_YA_ZHI_BO = "com.duowan.kiwi";    //虎牙直播
    public static final String YING_KE_ZHI_BO = "com.meelive.ingkee";   //映客直播
    public static final String MIAO_PAI = "com.yixia.videoeditor";      //秒拍
    public static final String MEI_TU_XIU_XIU = "com.mt.mtxx.mtxx";     //美图秀秀
    public static final String MEI_YAN_XIANG_JI = "com.meitu.meiyancamera";     //美颜相机
    public static final String XIE_CHENG = "ctrip.android.view";    //携程
    public static final String MO_MO = "com.immomo.momo";       //陌陌
    public static final String YOU_KU = "com.youku.phone";      //优酷


    public static final String AI_QI_YI = "com.qiyi.video";     //爱奇艺
    public static final String AI_QI_YI_CLASSNAME = "org.qiyi.android.video.MainActivity";


    public static final String DI_DI = "com.sdu.didi.psnger";


    public static final String ZHI_FU_BAO = "com.eg.android.AlipayGphone";      //支付宝
    public static final String ZHI_FU_BAO_CLASSNAME = "com.eg.android.AlipayGphone.AlipayLogin";


    public static final String TAO_BAO = "com.taobao.taobao";       //淘宝
    public static final String TAO_BAO_CLASSNAME = "com.taobao.tao.homepage.MainActivity3";


    public static final String JING_DONG = "com.jingdong.app.mall";     //京东
    public static final String DA_ZONG_DIAN_PING = "com.dianping.v1";       //大众点评
    public static final String JIAN_SHU = "com.jianshu.haruki";     //简书


    public static final String BAI_DU_DI_TU = "com.baidu.BaiduMap";     //百度地图
    public static final String BAI_DU_DI_TU_CLASSNAME = "com.baidu.baidumaps.MapsActivity";


    public static final String GAO_DE_DI_TU = "com.autonavi.minimap";       //高德地图


    public static final String WEI_XIN = "com.tencent.mm";      //微信
    public static final String WEI_XIN_CLASSNAME = "com.tencent.mm.ui.LauncherUI";
    //com.tencent.mm/.ui.LauncherUI

    public static final String QQ = "com.tencent.mobileqq";     //QQ
    public static final String QQ_CLASSNAME = "com.tencent.mobileqq.activity.SplashActivity";


    public static final String WANG_YI_YUN_YIN_YUE = "com.netease.cloudmusic";      //网易云音乐
    public static final String WANG_YI_YUN_YIN_YUE_CLASSNAME = "com.netease.cloudmusic.activity.MainActivity";


    /**
     * 根据domain跳转APP
     * 跳转了就true，没跳转就false
     * @param context
     * @param domain
     * @return
     */
    public static boolean skipDomainAPP(Context context, String domain) {
        switch (domain) {
            case "movie":   //电影
            case "tv":  //电视剧
            case "animation":      //卡通
            case "tv_show":     //综艺
                return skipApp(AI_QI_YI, AI_QI_YI_CLASSNAME);   //爱奇艺
            case "takeaway":    //外卖
            case "groupbuy":    //团购
            case "cate":    //美食
            case "hotel":   //酒店
            case "travel":  //旅游
            case "train":   //列车
            case "flight":  //航班
                return skipApp(MEITUAN, MEITUAN_CLASSNAME);     //美团
            /*
            case "music":
            case "player":  //音乐播放
                return skipApp(WANG_YI_YUN_YIN_YUE, WANG_YI_YUN_YIN_YUE_CLASSNAME);     //网易云音乐
            */
            case "map":     //地图
                return skipApp(BAI_DU_DI_TU, BAI_DU_DI_TU_CLASSNAME);   //百度地图
        }
        return false;
    }

    /**
     * 直接根据APP名字跳转
     * 跳转了就true，没跳转就false
     * @param APPName
     * @return
     */
    public static boolean skipNameAPP(String APPName) {
        if (APPName != null && !APPName.isEmpty()) {
            /*
            if (APPName.indexOf("相机") != -1) {
                return skipApp(CAMERA, CAMERA_CLASSNAME);
            }
            */
            if (APPName.indexOf("淘宝") != -1) {
                return skipApp(TAO_BAO, TAO_BAO_CLASSNAME);
            }
            if (APPName.indexOf("微信") != -1) {
                return skipApp(WEI_XIN, WEI_XIN_CLASSNAME);
            }
            if (APPName.indexOf("QQ") != -1) {
                return skipApp(QQ, QQ_CLASSNAME);
            }
            if (APPName.indexOf("支付宝") != -1) {
                return skipApp(ZHI_FU_BAO, ZHI_FU_BAO_CLASSNAME);
            }
            /*if (APPName.indexOf("音乐") != -1) {
                return skipApp(WANG_YI_YUN_YIN_YUE, WANG_YI_YUN_YIN_YUE_CLASSNAME);
            }*/
            if (APPName.indexOf("美团") != -1) {
                return skipApp(MEITUAN, MEITUAN_CLASSNAME);
            }
            if (APPName.indexOf("地图") != -1) {
                return skipApp(BAI_DU_DI_TU, BAI_DU_DI_TU_CLASSNAME);
            }
            if (APPName.indexOf("知乎") != -1) {
                return skipApp(ZHI_HU, ZHI_HU_CLASSNAME);
            }
            if (APPName.indexOf("爱奇艺") != -1) {
                return skipApp(AI_QI_YI, AI_QI_YI_CLASSNAME);
            }
        }
        return false;
    }

    /**
     * 判断是否存在这个包
     * @param context
     * @param uri
     * @return
     */
    private static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * 统一的跳转APP方法，传入对应的packageName和className
     * @param packageName
     * @param className
     * @return
     */
    private static boolean skipApp(String packageName, String className){
        if (isAppInstalled(MyApplication.context, packageName)) {
            Intent intent = new Intent();
            intent.setAction("Android.intent.action.VIEW");
            intent.setClassName(packageName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.context.startActivity(intent);
            return true;
        } else {
            //Toast.makeText(MyApplication.context,"没有安装此软件",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "skipApp: 没有安装这个软件");
            return false;
        }
    }

}

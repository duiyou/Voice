package com.example.util;

import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;

/**
 * 时间工具类，用于根据自定义的词语得到准确的时间
 * 返回long类型
 * 大于0是识别到的时间
 * -1表示识别的时间错误
 * -2表示没有识别到时间
 *
 *
 * 问题：
 *      1、当不是正确日期的时候Date会自动转化为正确日期不会报错（2月31日自动转化为3月1日）
 *      2、用不到闰年判断
 *      3、不需要判断正不正确所以返回值没有-1
 */
public class TimeUtil {
    private static final String TAG = "TimeUtil";

    //常量：识别到了错误的时间
    public static final long ERRORDATE = -1;
    //常量：没有识别到时间
    public static final long NODATE = -2;


    //当前时间对象
    private static Date nowDate = new Date();
    //日期操作类
    private static Calendar calendar = new GregorianCalendar();
    //当前年份
    private static String nowYear = null;
    //当前星期，周一为1，周日为7
    private static int nowWeek = -1;
    //默认当前时间long
    public static Long nowTimeLong = nowDate.getTime();

    //用于时间加减的map
    private static Map<String, Integer> dataMap = new HashMap<>();
    static {

        //只是为了打印一下当前时间
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String s = df.format(nowDate);
        Log.i(TAG, "getChineseDay: 当前时间：" + s);

        //初始化当前年份
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        nowYear = dateFormat.format(nowDate);

        //获取当前星期几
        calendar.setTime(nowDate);
        //get获取一周的第几天，周日为1，周一为2
        int temp = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        nowWeek = temp == 0 ? 7 : temp;

        dataMap.put("今天", 0);
        dataMap.put("明天", +1);
        dataMap.put("后天", +2);
        dataMap.put("大后天", +3);

        dataMap.put("周一", (1 <= nowWeek ? 1 + 7 - nowWeek : 1 - nowWeek));
        dataMap.put("星期一", (1 <= nowWeek ? 1 + 7 - nowWeek : 1 - nowWeek));

        dataMap.put("周二", (2 <= nowWeek ? 2 + 7 - nowWeek : 2 - nowWeek));
        dataMap.put("星期二", (2 <= nowWeek ? 2 + 7 - nowWeek : 2 - nowWeek));

        dataMap.put("周三", (3 <= nowWeek ? 3 + 7 - nowWeek : 3 - nowWeek));
        dataMap.put("星期三", (3 <= nowWeek ? 3 + 7 - nowWeek : 3 - nowWeek));

        dataMap.put("周四", (4 <= nowWeek ? 4 + 7 - nowWeek : 4 - nowWeek));
        dataMap.put("星期四", (4 <= nowWeek ? 4 + 7 - nowWeek : 4 - nowWeek));

        dataMap.put("周五", (5 <= nowWeek ? 5 + 7 - nowWeek : 5 - nowWeek));
        dataMap.put("星期五", (5 <= nowWeek ? 5 + 7 - nowWeek : 5 - nowWeek));

        dataMap.put("周六", (6 <= nowWeek ? 6 + 7 - nowWeek : 6 - nowWeek));
        dataMap.put("星期六", (6 <= nowWeek ? 6 + 7 - nowWeek : 6 - nowWeek));

        dataMap.put("周日", (7 <= nowWeek ? 7 + 7 - nowWeek : 7 - nowWeek));
        dataMap.put("星期日", (7 <= nowWeek ? 7 + 7 - nowWeek : 7 - nowWeek));
        dataMap.put("周天", (7 <= nowWeek ? 7 + 7 - nowWeek : 7 - nowWeek));
        dataMap.put("星期天", (7 <= nowWeek ? 7 + 7 - nowWeek : 7 - nowWeek));

    }

    /**
     * 中文日期格式
     * day得到根据现在时间的日期
     * 正确：今天
     *      明天
     *      周一
     *      星期二
     * 错误：昨天
     */
    private static String ChinesePattern = "^[\\s\\S]*(?<day>今天|明天|后天|大后天|周[一,二,三,四,五,六,日,天]|星期[一,二,三,四,五,六,日,天])[\\s\\S]*$";

    /**
     * 月日格式
     * month是月份，day是日
     * 正确：3月8日
     * 错误：03月8日
     */
    private static String MMDDPattern = "^[\\s\\S]*(?<month>[1-9]|1[0-2])月(?<day>[1-9]|1[0-9]|2[0-9]|3[01])[号|日][\\s\\S]*$";

    /**
     * 正确的日期格式，自动判断是否对的日期（即包含闰月的判断）
     * 正确：2020-3-7
     *      19-12-2
     *      19-12-02
     *  错误：2020-2-30（没有30号）
     */
    private static String trueYYMMDDPattern = "^(((((1[6-9]|[2-9]\\d)?\\d{2})[\\.\\-\\/](0?[13578]|1[02])[\\.\\-\\/](0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)?\\d{2})[\\.\\-\\/](0?[13456789]|1[012])[\\.\\-\\/](0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)?\\d{2})[\\.\\-\\/]0?2[\\.\\-\\/](0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)[\\.\\-\\/]0?2[\\.\\-\\/]29))|((((1[6-9]|[2-9]\\d)?\\d{2})(0[13578]|1[02])(0[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)?\\d{2})(0[13456789]|1[012])(0[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)?\\d{2})02(0[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)0229)))$";

    /**
     * 根据文本识别时间
     * @param str
     * @return
     */
    public static long getTime(String str) {
        //首先去识别日期，有日期识别的话直接返回
        long result1 = getMMDD(str);
        if (result1 > 0) return result1;

        //准确日期没识别到，就去识别“明天”这一类词
        long result2 = getChineseDay(str);
        if (result2 >0) return result2;

        //啥都不行就没识别到时间，返回默认值
        return nowTimeLong;
    }


    /**
     * 得到文本中的“3月7日”这种日期
     * @param str
     * @return
     */
    private static long getMMDD(String str) {
        Pattern pattern = Pattern.compile(MMDDPattern);
        Matcher matcher = pattern.matcher(str);
        //如果符合日期的正则表达式，则去获取准确时间，然后再判断是否是闰年
        if (matcher.matches()) {
            //获取日期格式的月和日
            String month = matcher.group(1);
            String day = matcher.group(2);
            Log.i(TAG, "getMMDD: 识别到的月份：" + month + "，日：" + day);
            //构建格式化的年-月-日字符串
            String temp = nowYear + "-" + month + "-" + day;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                //把字符串转化为Date时间类
                Date d = df.parse(temp);
                //如果识别的这个时间比当前时间小，证明是明年的事情，去让年份+1
                if (d.getTime() < nowDate.getTime()) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(d);
                    c.add(Calendar.YEAR, 1);
                    d = c.getTime();
                }
                //识别到的
                String print = df.format(d);
                Log.i(TAG, "getMMDD: 识别到的准确日期："+ print);
                //最后直接返回这个时间
                return d.getTime();
            } catch (ParseException e) {
                //e.printStackTrace();
                Log.e(TAG, "getMMDD: 字符串转化为Date时间类出错");
            }
        }
        //没有符合就返回没有识别到时间
        return NODATE;
    }

    /**
     * 得到文本中“今天”这种日期
     * @param str
     * @return
     */
    private static long getChineseDay(String str) {
        Pattern pattern = Pattern.compile(ChinesePattern);
        Matcher matcher = pattern.matcher(str);
        //如果符合日期的正则表达式，则去获取准确时间，然后再判断是否是闰年
        if (matcher.matches()) {
            //获取日期格式的月和日
            String day = matcher.group(1);
            Log.i(TAG, "getChineseDay: 识别到的根据当前时间的日期：" + day);
            //获取这个中文根据当前时间需要加上的天数
            int addDay = dataMap.get(day);
            //加上这个天数
            calendar.add(Calendar.DATE, addDay);
            //得到根据当前天数加了以后的天数的Date时间类
            Date result = calendar.getTime();
            //加了以后就加回来，不能改变当前天数啊傻逼
            calendar.add(Calendar.DATE, -addDay);

            //只为了打印一下
            DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            String temp = df.format(result);
            Log.i(TAG, "getChineseDay: 识别到的准确日期：" + temp);

            //返回识别到的结果
            return result.getTime();
        }
        //没有符合就返回没有识别到时间
        return NODATE;
    }



    /**
     * 判断该日期是否符合闰年正则表达式
     * @param dateStr
     * @return
     */
    private static boolean isTrue(String dateStr) {
        return Pattern.matches(trueYYMMDDPattern, dateStr);
    }

}


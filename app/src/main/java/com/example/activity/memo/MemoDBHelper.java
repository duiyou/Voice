package com.example.activity.memo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.util.TimeUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库辅助类
 *
 * 注意：数据库表为id、time、context
 * 预编译语句从0开始
 * 所以查询时time的下标为1，context的下标为2
 */
public class MemoDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "MemoDBHelper";
    //备忘录数据库名
    private static String dataBaseName = "Voice.db";

    //自定义类的构造方法，需要调用super，没有此数据库则会创建
    public MemoDBHelper(Context context){
        super(context, dataBaseName, null, 1);
        //创建完了就创建表（SQL语句带有不重复创建的语句）
        createTable();

        //测试
        test();
    }

    /**
     * 数据库第一次创建时调用此方法
     * 即可在此方法创建对应的表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //只有不存在数据库时才调用
        Log.d(TAG, "onCreate: 创建了数据库啊");
    }

    /**
     * 数据库更新时调用
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //创建Memo表,time为时间的秒数，context为存储的文本
    private String createTableStr = "create table if not exists memo(memo_id integer primary key autoincrement," +
            "memo_time integer not null, memo_content text not null)";

    /**
     * 创建表
     */
    private void createTable(){
        //获取数据库对象
        SQLiteDatabase sd = this.getWritableDatabase();
        //执行创建表的语句
        sd.execSQL(createTableStr);
        sd.close();
    }

    /**
     * 用于测试
     */
    private void test(){
        SQLiteDatabase sd = this.getWritableDatabase();

        //测试删除表
        //sd.execSQL("drop table memo");

        //测试插入数据
        //insert(new Message(1l, "5", "你好"));

        //测试删除数据
        //delete(new Message(4l, "", ""));

        //测试更新数据
        //update(new Message(2l, "6", "说啥呢"));

        //测试查询数据
        //selectAll();

        sd.close();
    }

    //插入的预编译语句
    private String insertStr = "insert into memo(memo_time, memo_content) values(?,?);";
    /**
     * 插入数据，时间，内容
     * executeInsert返回的是插入的行的ID
     * @param message
     */
    public Message insert(Message message) {
        //调用timeStrToLong()把字符串格式转化为Long
        long time = timeStrToLong(message.getTime());
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(insertStr);
        statement.bindLong(1, time);
        statement.bindString(2, message.getContent());
        long result = statement.executeInsert();
        Log.i(TAG, "insert: 插入的行的ID：" + result + "，插入是否成功：" + (result != 0 ? true : false));
        statement.close();
        db.close();
        //把传过来的Message对象设置他的id然后重新传回去
        message.setId(result);
        return message;
    }

    //删除语句
    private String deleteStr = "delete from memo where memo_id=?;";
    /**
     * 删除一条数据
     * executeUpdateDelete返回受影响的行数
     * @return
     */
    public boolean delete(Message message) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(deleteStr);
        statement.bindLong(1, message.getId());
        int result = statement.executeUpdateDelete();
        Log.i(TAG, "delete: 受影响的行数：" + result + "删除是否成功" + (result != 0 ? true : false));
        statement.close();
        db.close();
        if (result != 0) return true;
        return  false;
    }

    //更新语句
    private String updateStr = "update memo set memo_time=? , memo_content=? where memo_id=?;";
    /**
     * 删除一条数据
     * executeUpdateDelete返回受影响的行数
     * @return
     */
    public boolean update(Message message) {
        //调用timeStrToLong()把字符串转化为Long
        long time = timeStrToLong(message.getTime());
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(updateStr);
        statement.bindLong(1, time);
        statement.bindString(2, message.getContent());
        statement.bindLong(3, message.getId());
        int result = statement.executeUpdateDelete();
        Log.i(TAG, "update: 受影响的行数：" + result + "更新是否成功" + (result != 0 ? true : false));
        statement.close();
        db.close();
        if (result != 0) return true;
        return  false;
    }


    //查询语句，order by默认升序排序
    private String selectAllStr = "select * from memo order by memo_time";
    /**
     * 查询所有数据
     * @return
     */
    public List<Message> selectAll(){
        List<Message> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllStr, null);
        if (cursor!= null && cursor.getCount()>0) {
            while (cursor.moveToNext()) {
                //columnIndex代表列的索引
                long id = cursor.getLong(0);
                Long timeLong = cursor.getLong(1);
                /************调用timeLongToStr()进行时间转换************************/
                String timeStr = timeLongToStr(timeLong);
                String context = cursor.getString(2);
                list.add(new Message(id, timeStr, context));
                Log.i(TAG, "selectAll: " + "id：" + id + "，时间：" + timeStr + "，内容：" + context);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据数据的id然后得到查询过后排序的下标
     * （一定会找到的，因为就时找排序后的那个id对应的下标）
     * @return
     */
    public int selectById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAllStr, null);
        if (cursor!= null && cursor.getCount()>0) {
            int i = 0;
            while (cursor.moveToNext()) {
                if (cursor.getLong(0) == id) {
                    Log.i(TAG, "selectById: 根据id查到排序后的下标index：" + i);
                    return i;
                }
                i++;
            }
        }
        cursor.close();
        db.close();
        return -1;
    }

    /**
     * 字符串的时间格式转化为long类型
     * @param timeStr
     * @return
     */
    private long timeStrToLong(String timeStr) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long timeLong = -1l;
        try {
            Date date = dateFormat.parse(timeStr);
            timeLong = date.getTime();
        } catch (ParseException e) {
            //e.printStackTrace();
            //字符串转化Long有错误则默认为当前时间
            timeLong = TimeUtil.nowTimeLong;
        }
        return timeLong;
    }

    /**
     * Long类型的时间转化为字符串的时间格式
     * @param timeLong
     * @return
     */
    private String timeLongToStr(Long timeLong) {
        //把Long转化为yyyy-MM-dd格式字符串
        Date tempDate = new Date(timeLong);
        DateFormat tempDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = tempDateFormat.format(tempDate);
        return timeStr;
    }
}

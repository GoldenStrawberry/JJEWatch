package so.wih.android.jjewatch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * ==============================================
 * Created by HuWei on 2016/12/21.
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class MessageDao {

    private static MessageDao messageDao;
    private final SQLiteDatabase db;
    //表的名称
    public static final String TABLE_FRIEND="message";
    //构造
    public MessageDao(Context context){
        MyMessageDBHelpter dbHelpter = new MyMessageDBHelpter(context,"messages.db",null,1);
        db = dbHelpter.getWritableDatabase();
    }
    //单例
    public static synchronized MessageDao getInstance(Context context){
        if(messageDao == null){
            messageDao = new MessageDao(context);
        }
        return messageDao ;
    }

    /**
     *  增加数据
     * @param senderId 电话号码
     */
    public void addMessage(String senderId){
        ContentValues values = new ContentValues();
        values.put("senderId", senderId); // KEY 是列名，vlaue 是该列的值
        // 参数一：表名，参数三，是插入的内容
        // 参数二：只要能保存 values 中是有内容的，第二个参数可以忽略
        db.insert(TABLE_FRIEND, null, values);
    }

    /**
     * 查询所有数据
     */
    public List<String> queryData(String senderId){
        List<String> phoneList = new ArrayList<>();
        String[] columns={"senderId"};
        String selection = "senderId"; //选择的条件
        String[] selectionArgs = {senderId}; //选择条件的参数
        String groupBy = null;  //组别
        String having = null;   //传递NULL将导致所有行组被包含，并且在不使用行分组时需要.。
        String orderBy = null;  //排序
        Cursor cursor = db.query(TABLE_FRIEND, columns, selection, selectionArgs, groupBy, having, orderBy);
        while(cursor.moveToNext()){
            String phone = cursor.getString(cursor.getColumnIndex("senderId"));
            phoneList.add(phone);
        }
        cursor.close();
        return phoneList ;
    }
    /**
     * 根据联系人名字删除联系人
     * @param senderId 联系人名字
     */
    public void deleteData(String senderId){
        String whereClause="senderId = ?"; //条件
        String[] whereArgs={senderId};          //条件参数
        db.delete(TABLE_FRIEND,whereClause,whereArgs);
    }
}

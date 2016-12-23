package so.wih.android.jjewatch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ==============================================
 * Created by HuWei on 2016/12/21.
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class FriendsDBHelpter extends SQLiteOpenHelper {
    private static FriendsDBHelpter friendsDBHelpter ;

    public FriendsDBHelpter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getInstance(Context context){
        if(friendsDBHelpter == null){
            friendsDBHelpter = new FriendsDBHelpter(context,"friends.db",null,1);
        }
        return friendsDBHelpter.getWritableDatabase() ;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table friend(_id integer primary key autoincrement," +
                "fname varchar(10) number varchar(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

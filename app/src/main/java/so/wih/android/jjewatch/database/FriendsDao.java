package so.wih.android.jjewatch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * ==============================================
 * Created by HuWei on 2016/12/21.
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class FriendsDao {

    private static FriendsDao friendsDao;
    private final SQLiteDatabase db;
    //构造
    public FriendsDao(Context context){
        FriendsDBHelpter dbHelpter = new FriendsDBHelpter(context,"friends.db",null,1);
        db = dbHelpter.getWritableDatabase();
    }
    //单例
    public static synchronized FriendsDao getInstance(Context context){
        if(friendsDao == null){
            friendsDao = new FriendsDao(context);
        }
        return friendsDao ;
    }
}

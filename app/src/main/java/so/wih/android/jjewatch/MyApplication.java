package so.wih.android.jjewatch;

import android.app.Application;
import android.content.Context;

import io.rong.imkit.RongIM;

import static so.wih.android.jjewatch.utils.CommonUtils.getCurProcessName;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MyApplication extends Application{
    private static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            //初始化融云
            RongIM.init(this);
            AppContexts.init(this);
        }



    }

    public static Context getCtx() {
        return ctx;
    }

}

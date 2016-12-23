package so.wih.android.jjewatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import so.wih.android.jjewatch.ui.activity.AMAPLocationActivity;
import so.wih.android.jjewatch.utils.Constants;

/**
 * Created by Administrator on 2016/12/6.
 */

public class AppContexts implements RongIM.LocationProvider, RongIMClient.ConnectionStatusListener, RongIMClient.OnReceiveMessageListener {

    private Context mContext;
    private static ArrayList<Activity> mActivities;
    private static AppContexts mAppContexts;
    private LocationCallback mLastLocationCallback;
    public AppContexts(Context mContext){
        this.mContext = mContext ;
        initListener();
        mActivities = new ArrayList<>();
    }
    public static void init(Context context) {

        if (mAppContexts == null) {
            synchronized (AppContexts.class) {

                if (mAppContexts == null) {
                    mAppContexts = new AppContexts(context);
                }
            }
        }

    }
    /**
     * 获取AppContexts实例。
     *
     * @return AppContexts。
     */
    public static AppContexts getInstance() {
        return mAppContexts;
    }

    private void initListener() {

        RongIM.setLocationProvider(this);
        RongIM.setConnectionStatusListener(this);
        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);//设置消息接收监听器。
//        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());


    }

    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        this.mLastLocationCallback = locationCallback;
        Intent intent = new Intent(context, AMAPLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }
    @Override
    public boolean onReceived(Message message, int i) {

        Log.d(Constants.TAG, "AppContexts 方法 onReceived: 我执行了接收");
        messageListener.receiveMessage(message);


        return true;
    }

    private OnReceiveMessageListener messageListener ;

    public void setOnReceiveMessageListener(OnReceiveMessageListener receiveMessageListener){
        messageListener = receiveMessageListener;
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        Log.d(Constants.TAG, "AppContexts 方法 onChanged: "+connectionStatus.getValue());
    }

    public interface OnReceiveMessageListener{
        void receiveMessage(Message message);
    }

    /**
     * 管理Activity
     * @param activity
     */
    public void pushActivity(Activity activity) {
        mActivities.add(activity);
    }

    /**
     * 结束一个Activity
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            activity.finish();
            mActivities.remove(activity);
        }
    }

    /**
     * 结束所有的Activity
     */
    public void popAllActivity() {
        try {
            for (Activity activity : mActivities) {
                if (activity != null) {
                    activity.finish();
                }
            }
            mActivities.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

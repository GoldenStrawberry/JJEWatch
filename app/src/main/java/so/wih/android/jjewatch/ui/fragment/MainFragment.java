package so.wih.android.jjewatch.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import so.wih.android.jjewatch.MyApplication;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.api.util.RetrofitUtil;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LocationMessageUtils;
import so.wih.android.jjewatch.utils.LogUtils;
import so.wih.android.jjewatch.utils.MyToast;
import so.wih.android.jjewatch.utils.ServerHelpter;

import static java.text.DateFormat.getDateInstance;

/**
 * 主页面，时间界面
 * Created by Administrator on 2016/11/22.
 */

public class MainFragment extends Fragment implements SensorEventListener{

    private View mainView;
    private TextView tv_time;
    private TextView tv_year;
    private TextView tv_city;
    private TextView tv_weather;
    private TextView tv_pm25;
    private SharedPreferences sp;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    //城市信息
                    String city = aMapLocation.getCity();
                    sp.edit().putString(Constants.WEATHER_CITY,city).apply();
                    tv_city.setText(city);
                    RetrofitUtil.getWeather(sp,city,tv_pm25,tv_weather);
                    LogUtils.logdHu("onLocationChanged 城市信息: "+aMapLocation.getCity());
                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        }
    };
   /* //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL = 500;
    private Messenger messenger;
    private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
    private Handler delayHandler;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                messenger = new Messenger(service);
                Message msg = Message.obtain(null, Constants.MSG_FROM_CLIENT);
                msg.replyTo = mGetReplyMessenger;
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };*/
    private SensorManager mSensorManager;

   /* @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.MSG_FROM_SERVER:
                // 更新界面上的步数 数据
                int step = msg.getData().getInt("step");
                tvStep.setText(step+ "步");
                sp.edit().putInt(Constants.MY_STEP,step).apply();
                delayHandler.sendEmptyMessageDelayed(Constants.REQUEST_SERVER, TIME_INTERVAL);
                break;
            case Constants.REQUEST_SERVER:
                try {
                    Message msg1 = Message.obtain(null, Constants.MSG_FROM_CLIENT);
                    msg1.replyTo = mGetReplyMessenger;
                    messenger.send(msg1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
        return false;
    }*/

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String hour_time = (String) msg.obj;
            tv_time.setText(hour_time);
        }
    };
    private TextView tvStep;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.main_fragment, null);
        sp = getContext().getSharedPreferences(Constants.JJE_CONFIG, Context.MODE_PRIVATE);
        //初始化控件
        initView();
        //设置数据
        setData();
//        delayHandler = new Handler(this);
        netWorkIsOK();

        return mainView;
    }
    /**
     * 初始化控件
     */
    private void initView() {
        //时间
        tv_time = (TextView) mainView.findViewById(R.id.tv_time);
        //年月日
        tv_year = (TextView) mainView.findViewById(R.id.tv_year);
        //城市
        tv_city = (TextView)mainView.findViewById(R.id.tv_city);
        tv_weather = (TextView)mainView.findViewById(R.id.tv_weather);
        tv_pm25 = (TextView)mainView.findViewById(R.id.tv_pm25);
        //记步
        tvStep = (TextView) mainView.findViewById(R.id.tv_step);

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    /**
     * 设置数据
     */
    private void setData() {

        String week = CommonUtils.getWeek();
        DateFormat dateInstance = getDateInstance();
        String obj_year = dateInstance.format(new Date());
        sp.edit().putString(Constants.LOCAL_DATE,obj_year).apply();
        String[] year = obj_year.split("年");
        tv_year.setText(year[1]+" "+week);
        //耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
                        String str=sdf.format(new Date());
                        Message msg = new Message();
                        msg.obj = str ;
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void netWorkIsOK(){
        //获取今天的日期
        String w_date = CommonUtils.FormatTime();

        String wheather_date = sp.getString(Constants.WEATHER_DATE, "");
        if(w_date.equals(wheather_date)){
            //获取步数
            int step = sp.getInt(Constants.MY_STEP, 0);
            tvStep.setText(step+"步");
        }else{
            tvStep.setText("0步");
            sp.edit().putInt(Constants.MY_STEP,0).apply();
        }

        if(CommonUtils.networkAvaliable(getContext()) != null ){
            if(CommonUtils.networkAvaliable(getContext()).isConnected()){ //网络是否连接.
                int anInt = sp.getInt(Constants.PARSEINT, -1);
                String request_hour=CommonUtils.FormatTime4(System.currentTimeMillis());
                int parseInt = Integer.parseInt(request_hour);
                if(w_date.equals(wheather_date) && parseInt - anInt < 1 ){
                    //1个小时里读取SP里的数据,每隔1小时更新
                    String city = sp.getString(Constants.WEATHER_CITY, "");
                    String weather = sp.getString(Constants.WEATHER_WEATHER1, "");
                    String temperature = sp.getString(Constants.WEATHER_TEMPERA, "");
                    String pm25 = sp.getString(Constants.WEATHER_PM25, "");
                    tv_city.setText(city);
                    tv_weather.setText(weather+temperature);
                    tv_pm25.setText("PM2.5 "+pm25);
                }else{
                    //初始化定位
                    mLocationClient = new AMapLocationClient(MyApplication.getCtx());
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                Constants.MY_PERMISSIONS_REQUEST_LOCATION);
                    }else{
                        LocationMessageUtils.getLocalMessage(mLocationClient,mLocationListener);
                    }
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
                    String request_hour2=sdf2.format(new Date());
                    int parseInt2 = Integer.parseInt(request_hour2);
                    sp.edit().putInt(Constants.PARSEINT,parseInt2).apply();
                }
            }
        }else{
            tv_city.setText("无网络");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //版本信息
        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            return;
        }else{
            MyToast.showToast(getContext(),"有新版本可更新，请到设置里更新版本。");
        }
        UserInfo myInfo = JMessageClient.getMyInfo();
        if(myInfo != null){
            LogUtils.logdHu("登录的信息"+myInfo.getUserName());
        }else{
            LogUtils.logdHu("没有登录信息");
            //登录JMessage
            ServerHelpter.getWatchInfo(sp);
        }
//        setupService();
        // 调用 JPush 接口来设置别名。
        JPushInterface.setAliasAndTags(MyApplication.getCtx(),
                sp.getString(Constants.LOGIN_PHONE,""),
                null,
                mAliasCallback); // 调用 JPush 接口来设置别名。

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i("huwei", logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    int anInt = sp.getInt(Constants.ACTIVE_NO, -1);
                    if(anInt==0){
                        ServerHelpter.watchActiveTimer(sp);
                    }
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("huwei", logs);
                    // TODO 延迟 60 秒来调用 Handler 设置别名
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("huwei", logs);
            }
//            ExampleUtil.showToast(logs, getApplicationContext());
        }
    };

/*    private void setupService() {
        Intent intent = new Intent(getActivity(), StepService.class);
        getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);
    }*/

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int values = (int)event.values[0];
        tvStep.setText(values+"步");
        sp.edit().putInt(Constants.MY_STEP,values).apply();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
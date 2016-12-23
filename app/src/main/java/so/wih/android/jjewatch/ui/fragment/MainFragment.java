package so.wih.android.jjewatch.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import so.wih.android.jjewatch.R;

/**
 * 主页面，时间界面
 * Created by Administrator on 2016/11/22.
 */

public class MainFragment extends Fragment {

    private View mainView;
    private Handler handler ;
    private TextView tv_time;
    private TextView tv_year;
    private ImageView iv_end_call;
    private ImageView iv_call;
    private TextView tv_battery;
    private ImageView iv_battery;
    private PowerConnectionReceiver batteryReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.main_fragment, null);
        //电池广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new PowerConnectionReceiver();
        getActivity().registerReceiver(batteryReceiver, intentFilter);
        //初始化控件
        initView();
        //设置数据
        setData();
        return mainView;
    }
    public class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();

        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        View watchView = mainView.findViewById(R.id.myview);
        //时间
        tv_time = (TextView) mainView.findViewById(R.id.tv_time);
        //年月日
        tv_year = (TextView) mainView.findViewById(R.id.tv_year);
        //挂断电话
        iv_end_call = (ImageView) mainView.findViewById(R.id.iv_call_end);
        //打电话、接电话
        iv_call = (ImageView) mainView.findViewById(R.id.iv_call);
        //电量文字
        tv_battery = (TextView) mainView.findViewById(R.id.tv_battery);
        //电池状态:充电or未充
        iv_battery = (ImageView)mainView.findViewById(R.id.iv_battery);

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日&HH:mm:ss");
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

    /**
     * 设置数据
     */
    private void setData() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String obj_time = (String) msg.obj;
                String[] strings = obj_time.split("&");
                tv_year.setText(strings[0]);
                tv_time.setText(strings[1]);
            }
        };

    }

    private float mBatteryLevel;
    private float mBatteryScale;
    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                //充电状态
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ;
                if(isCharging){
                    //mTextView.setText("正在充电...");
                }else{
                    //mTextView.setText("未充电");
                }
                //电池电量
                //获取当前电量
                float mBatteryLevel = intent.getIntExtra("level", 0);
                //电量的总刻度
                float mBatteryScale = intent.getIntExtra("scale", 100);
                int percent = (int) ((mBatteryLevel / mBatteryScale) * 100);
                tv_battery.setText(percent+"% ");

            }
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(batteryReceiver);
        super.onDestroy();
    }
}

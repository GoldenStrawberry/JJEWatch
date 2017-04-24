package so.wih.android.jjewatch.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tencent.bugly.beta.Beta;

import java.lang.reflect.Method;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.api.util.RetrofitUtil;
import so.wih.android.jjewatch.bean.Response;
import so.wih.android.jjewatch.service.LocationService;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LogUtils;
import so.wih.android.jjewatch.utils.MyToast;
import so.wih.android.jjewatch.utils.NotificationUtils;
import so.wih.android.jjewatch.utils.ServerHelpter;

/**
 * 设置页面
 * Created by HuWei on 2016/11/29.
 */

public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
        View.OnClickListener {

    @BindView(R.id.btn_wifi)
    Button btnWifi;
    @BindView(R.id.sb_sys_voice)
    SeekBar sbSysVoice;
    @BindView(R.id.sb_sunlight)
    SeekBar sbSunlight;
    @BindView(R.id.tv_voice_value)
    TextView tvVoiceValue;
    @BindView(R.id.tv_sunlight_value)
    TextView tvSunlightValue;
    @BindView(R.id.tv_encoder)
    TextView tvEncoder;
    @BindView(R.id.btn_gprs)
    Button btnGprs;
    @BindView(R.id.btn_update)
    Button btnUpdate;

    private AudioManager mAudioManager;
    private int maxVolume;
    private String data;
    private String data2;
    private Intent locationIntent;
    private SharedPreferences sp;


    @Override
    public void init() {
        super.init();
        if (locationIntent == null) {
            locationIntent = new Intent(this, LocationService.class);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initData() {
        sp = getSharedPreferences(Constants.JJE_CONFIG, MODE_PRIVATE);
        //音频管理器
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //最大音量
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sbSysVoice.setMax(maxVolume);

        //当前音量
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        data = intToString(currentVolume, maxVolume);
        tvVoiceValue.setText(data + "%");

        try {
            //获取当前亮度,获取失败
            int intScreenBrightness = (Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS));
            sbSunlight.setMax(255);
            sbSunlight.setProgress(intScreenBrightness);

            data2 = intToString(intScreenBrightness, 255);
            tvSunlightValue.setText(data2 + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
        sbSysVoice.setProgress(currentVolume);
    }

    @Override
    public void initListener() {
        //进度改变监听
        sbSysVoice.setOnSeekBarChangeListener(this);
        sbSunlight.setOnSeekBarChangeListener(this);
        //生成二维码
        tvEncoder.setOnClickListener(this);
        btnWifi.setOnClickListener(this);
        btnGprs.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        int id = seekBar.getId();
        switch (id) {
            case R.id.sb_sys_voice://改变系统声音
                //有了权限，具体的动作
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                NotificationUtils.ringMode(this);
                data = intToString(progress, maxVolume);
                tvVoiceValue.setText(data + "%");

                break;
            case R.id.sb_sunlight: //改变系统亮度
                //设置系统亮度
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!Settings.System.canWrite(context)) {
//                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                        intent.setData(Uri.parse("package:" + context.getPackageName()));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    } else {
                //有了权限，具体的动作
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, progress);
                data2 = intToString(progress, 255);
                tvSunlightValue.setText(data2 + "%");
//                    }
//                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * @param current
     * @param max
     */
    private String intToString(int current, int max) {
        double dat = ((double) current / (double) max) * 100;
        DecimalFormat df = new DecimalFormat("#");
        String dat2 = df.format(dat);
        return dat2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_encoder:
                // 生成二维码
                Intent intent = new Intent(context, QRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_wifi:
                //请求wifi
                getWIFIinfo();
                break;
            case R.id.btn_gprs:
                //打开网络
                openGPRS();
                break;
            case R.id.btn_update:
                /***** 检查更新 *****/
                Beta.checkUpgrade();
                break;
        }

    }

    /**
     * 打开网络
     */
    private void openGPRS() {
        //获取相应的服务
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = tm.getSimOperatorName();
        if (operatorName != "") { //有sim卡
            //打开网络
            Intent inte = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            startActivity(inte);
        } else {
            MyToast.showToast(context, "没有插入SIM卡!");
        }
    }


    private void getWIFIinfo() {
        //肯定是要获取的
        /*String username = "18626255712";
        String appKey = "04ddc14b3854ec337ea5c5b3";
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put(Constants.TYPE, Constants.EXTRA_WIFI_REQUEST);
        Message singleCustomMessage = JMessageClient.createSingleCustomMessage(username, appKey, valuesMap);
        JMessageClient.sendMessage(singleCustomMessage);*/

        int battery = sp.getInt(Constants.BATTERY, 0);
        String lat = sp.getString(Constants.LATITUDE, "");
        String lon = sp.getString(Constants.LONGITUDE, "");
        int wuid = sp.getInt(Constants.WATCH_USER_ID, -1);
        RetrofitUtil.getWatchAPI().watchGetWifi(lat, lon, battery, wuid, ServerHelpter.qrCode, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        if ("User Offline".equals(response.message)) {
                            MyToast.showToast(context, "用户不在线！");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("获取WIFI" + throwable.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    //检测GPRS是否打开
    private static boolean gprsIsOpenMethod(ConnectivityManager mCM, String methodName) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;

        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod(methodName, argClasses);

            isOpen = (Boolean) method.invoke(mCM, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }
}

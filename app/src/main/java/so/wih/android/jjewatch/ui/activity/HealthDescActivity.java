package so.wih.android.jjewatch.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.myinterface.MyCallBack;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.ServerHelpter;

/**
 * ==============================================
 * Created by HuWei on 2016/12/29.
 * 健康详情页
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class HealthDescActivity extends BaseActivity implements SensorEventListener {

    @BindView(R.id.tv_step)
    TextView tvStep;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.unit)
    TextView unit;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_result)
    TextView tvResult;
    private SensorManager mSensorManager;
    private SharedPreferences sp;
    private int wuid;
    private int position;
    private String type;


    @Override
    public int getLayoutResId() {
        return R.layout.activity_health_desc;
    }

    @Override
    public void initData() {
        sp = getSharedPreferences(Constants.JJE_CONFIG,MODE_PRIVATE);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        type = intent.getStringExtra(Constants.TYPE);
        wuid = sp.getInt(Constants.WATCH_USER_ID, -1);
        //传感器管理类
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(position == 1){
            String rateAndTime = sp.getString(Constants.HEART_RATE, "");
            tvResult.setText(rateAndTime);
        }
    }

    private void doAnimation() {
        AnimationSet as=new AnimationSet(true);
        AlphaAnimation al=new AlphaAnimation(1f,0.5f);
        //1代表完全不透明，0代表完全透明
        al.setDuration(800);
        al.setRepeatMode(Animation.REVERSE);
        al.setRepeatCount(Integer.MAX_VALUE);
        as.addAnimation(al);
        ivIcon.startAnimation(as);
    }

    private void scaleImage(int draw) {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, draw);
        //缩放
        bitmap = CommonUtils.getBitmap(bitmap, 100, 100);
        ivIcon.setImageBitmap(bitmap);
    }

    @Override
    public void initListener() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (position) {
            case 0:
                unit.setText("步");
                scaleImage(R.drawable.run_64);
                //赋值
                int step = sp.getInt(Constants.MY_STEP, 0);
                tvStep.setText(step+"");
                break;
            case 1:
                mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE), 3);
                scaleImage(R.drawable.heart_rate_64);
                doAnimation();
                tvState.setText("请正确佩戴手表！");
                break;
            case 2:
                scaleImage(R.drawable.blood_pressure_64);
                tvStep.setText("N");
                unit.setText("mmHg");
                break;
            case 3:
                scaleImage(R.drawable.blood_glucose_64);
                tvStep.setText("N");
                unit.setText("mmol/L");
                break;
        }
    }
    int i = 0 ;
    @Override
    public void onSensorChanged(SensorEvent event) {
        //判断传感器类别
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_COUNTER://临近传感器
                Log.d("huwei", "记步: " + Float.toString(event.values[0]));
                break;
            case Sensor.TYPE_HEART_RATE://心率传感器
                final int value = (int) event.values[0];
                if (value != 0) {
                    tvState.setVisibility(View.INVISIBLE);
                    String time3 = CommonUtils.FormatTime3(System.currentTimeMillis());
                    tvStep.setText(String.valueOf(value));
                    unit.setText("bpm");
                    if(i == 0){
                        String time2 = CommonUtils.FormatTime2(System.currentTimeMillis());
                        tvResult.setText(time2+" "+ String.valueOf(value));
                        sp.edit().putString(Constants.HEART_RATE,time2+" "+ String.valueOf(value)).apply();
                        //获取测量时间
                        ServerHelpter.upLoadBpm(value, time3, wuid, new MyCallBack() {
                            @Override
                            public void sucess() {
                                i++;
                                if(type != null){
                                    CommonUtils.replyPhone(type,Constants.EXTRA_HEART_RATE,value+"");
                                }
                            }
                            @Override
                            public void failed() {
                                i = 0 ;
                            }
                        });
                    }
                } else {
                    i=0 ;
                    tvState.setVisibility(View.VISIBLE);
                    tvState.setText("正在测量...");
                    tvStep.setText("");
                    unit.setText("");
                }
                Log.d("huwei", "心率： " + Float.toString(event.values[0]));
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

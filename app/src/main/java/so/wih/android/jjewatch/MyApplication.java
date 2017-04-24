package so.wih.android.jjewatch;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.exceptions.RealmMigrationNeededException;
import so.wih.android.jjewatch.receiver.StateReceiver;
import so.wih.android.jjewatch.service.LocationService;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MyApplication extends MultiDexApplication {
    private static Context ctx;
    private StateReceiver stateReceiver;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
        //============初始化极光JMessage=====
        JMessageClient.init(this);
        JMessageClient.setDebugMode(true);
        //==============JPush==============
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //=============初始化科大讯飞==============
        StringBuffer param = new StringBuffer();
        param.append("appid="+getString(R.string.app_id));
        param.append(",");
        // 设置使用v5+
        param.append(SpeechConstant.ENGINE_MODE+"="+ SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());
        initRealm();
//===========================Bugly==========================
        Bugly.init(getApplicationContext(), "a59ffabf9b", false);
        //自动初始化开关
        Beta.autoInit = true;
        //自动检查更新开关
        Beta.autoCheckUpgrade = true;
        //升级检查周期设置
        Beta.upgradeCheckPeriod = 60 * 1000;

//=====================================电池电量==============================
        IntentFilter filter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if(stateReceiver == null){
            stateReceiver = new StateReceiver();
        }
        getCtx().registerReceiver(stateReceiver, filter);//注册BroadcastReceiver

//===================================上传位置电量=============================
        Intent intent = new Intent(getCtx(), LocationService.class);
        getCtx().startService(intent);

//=====================================是否有电话卡===========================
        //获取相应的服务
/*        TelephonyManager tm = (TelephonyManager) getCtx().getSystemService(Context.TELEPHONY_SERVICE);
        String operatorName = tm.getSimOperatorName();
        LogUtils.logdHu(operatorName);
        if( operatorName != ""){ //有sim卡
            //电话状态
            if(myPhoneCallListener == null){
                myPhoneCallListener = new MyPhoneCallListener();
            }
            tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        }else{
            MyToast.showToast(getCtx(),"没有SIM卡");
        }*/
//=========================================================================

    }

    public static Context getCtx() {
        return ctx;
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("myMessage.realm")//配置名字
                .schemaVersion(2)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                    }
                })
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        try {
            Realm realm = Realm.getDefaultInstance();
            realm.close();
        } catch (RealmMigrationNeededException e) {
            e.printStackTrace();
        }
    }
}

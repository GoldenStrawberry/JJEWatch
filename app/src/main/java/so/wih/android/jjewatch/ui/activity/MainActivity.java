package so.wih.android.jjewatch.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.ui.fragment.AppFragment;
import so.wih.android.jjewatch.ui.fragment.CameraFragment;
import so.wih.android.jjewatch.ui.fragment.ContactFragment;
import so.wih.android.jjewatch.ui.fragment.HealthManagerFragment;
import so.wih.android.jjewatch.ui.fragment.MainFragment;
import so.wih.android.jjewatch.ui.fragment.SettingFragment;
import so.wih.android.jjewatch.ui.fragment.VoiceFunctionFragment;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LogUtils;
import so.wih.android.jjewatch.utils.MyToast;
import so.wih.android.jjewatch.utils.WifiAdmin;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> fragments ;
    private WifiAdmin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//=======================接收极光消息===============================
        JMessageClient.registerEventReceiver(this);
        //使用ViewPager填充
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        initdata();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(fragments.size()/2);

        admin = new WifiAdmin(this);

/*        String wifiName ="TP-LINK_888";
        String wifiPsw ="tplink1234";
        admin.connectWiFi(wifiName,wifiPsw);*/

    }

    /**
     * 加载数据
     */
    private void initdata() {
        fragments = new ArrayList<>();
        MainFragment mainFragment = new MainFragment();
        ContactFragment contactFragment = new ContactFragment();
        VoiceFunctionFragment voiceFunctiontFragment = new VoiceFunctionFragment();
        HealthManagerFragment healthManagerFragment = new HealthManagerFragment();
        AppFragment appFragment = new AppFragment();
        CameraFragment cameraFragment = new CameraFragment();
        SettingFragment settingFragment = new SettingFragment();

        fragments.add(settingFragment);
        fragments.add(appFragment);
        fragments.add(contactFragment);
        fragments.add(mainFragment);
        fragments.add(voiceFunctiontFragment);
        fragments.add(cameraFragment);
        fragments.add(healthManagerFragment);
    }

    /**
     *  viewpager的适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    public void onEvent(MessageEvent event){
        Message msg = event.getMessage();//获取消息对象
        UserInfo fromUser = msg.getFromUser();
        String userName = fromUser.getUserName();
        String address = fromUser.getAddress();
        LogUtils.logdHu("MainActivity里的方法onEvent()调用了"+userName+address);
        switch (msg.getContentType()){
            case text:
                //处理文字消息
                TextContent textContent = (TextContent) msg.getContent();
                String extra = textContent.getStringExtra("jiguang");
                String text = textContent.getText();
                Log.d("huwei", "CHAT : onEvent: "+text+"; 额外 : "+extra);
                break;
            case image:
                //处理图片消息
                ImageContent imageContent = (ImageContent) msg.getContent();
                imageContent.getLocalPath();//图片本地地址
                imageContent.getLocalThumbnailPath();//图片对应缩略图的本地地址
                break;
            case voice:
                //处理语音消息
                VoiceContent voiceContent = (VoiceContent) msg.getContent();
                String stringExtra = voiceContent.getStringExtra("XiaoGou");
                String localPath = voiceContent.getLocalPath();//语音文件本地地址
                int duration = voiceContent.getDuration();//语音文件时长
                Log.d("huwei", "接收到了语音消息："+duration+stringExtra+"接;"+localPath);
                break;
            case custom:
                //处理自定义消息
                CustomContent customContent = (CustomContent) msg.getContent();
                String ssid = customContent.getStringValue(Constants.WIFI_SSID);
                String pwd = customContent.getStringValue(Constants.WIFI_PWD);
                String heartRate = customContent.getStringValue(Constants.EXTRA_HEART_RATE);
                if(ssid != null && pwd != null){
                    LogUtils.logdHu("收到了自定义消息WIFI:"+ssid+" ; "+pwd);
                    boolean wifiAvailable = admin.connectWiFi(ssid,pwd);

                    if(wifiAvailable){
                        MyToast.showToast(MainActivity.this,"已连接WIFI");
                        CommonUtils.replyPhone(address,Constants.EXTRA_WIFI_REQUEST,"已连接WIFI");
                    }else{
                        CommonUtils.replyPhone(address,Constants.EXTRA_WIFI_REQUEST,"未连接WIFI");
                    }

                }
                if(heartRate != null){
                    Intent intent = new Intent(this,HealthDescActivity.class);
                    intent.putExtra("position",1);
                    intent.putExtra(Constants.TYPE,address);
                    startActivity(intent);
                }

                break;
            case eventNotification:
                //处理事件提醒消息
                EventNotificationContent eventNotificationContent = (EventNotificationContent)msg.getContent();
                switch (eventNotificationContent.getEventNotificationType()){
                    case group_member_added:
                        //群成员加群事件
                        break;
                    case group_member_removed:
                        //群成员被踢事件
                        break;
                    case group_member_exit:
                        //群成员退群事件
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(admin.mWifiManager != null){
            admin.mWifiManager.disconnect();
        }
        super.onDestroy();
    }
}

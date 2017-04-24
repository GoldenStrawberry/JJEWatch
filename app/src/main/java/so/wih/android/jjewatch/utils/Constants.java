package so.wih.android.jjewatch.utils;

import android.net.Uri;

/**
 * Created by HuWei on 2016/12/2.
 * 常量
 */

public class Constants {
    public static final String TAG = "huwei";

    /**
     * 申请权限用到的常量
     */
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 115;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 116;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 117;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 118;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 119;
    /**
     * 记步器用到的常量
     */
    public static final int MSG_FROM_CLIENT = 0;
    public static final int MSG_FROM_SERVER = 1;
    public static final int REQUEST_SERVER = 2;
    //sp文件名
    public static final String JJE_CONFIG = "jje_config";
    /**
     * 生成二维码是需要的设备类型
     */
    public static final String WATCH_DEVICE = "G01";

    /**
     * 登陆的电话号码
     */
    public static final String LOGIN_PHONE = "login_phone";


//==========================天气============================
    public static final String WEATHER_DATE = "date"; //日期
    public static final String WEATHER_PM25 = "pm25";
    public static final String WEATHER_CITY = "currentCity";
    public static final String WEATHER_TEMPERA = "temperature";
    public static final String WEATHER_WEATHER1 = "weather1";
    public static final String PARSEINT = "request_hour";
    public static final String MY_STEP = "step";
    public static final String LOCAL_DATE = "localdate";
    public static final String WATCH_USER_ID = "wuid";
    public static final String BATTERY = "battery";
    //上传位置和电量的时间
    public static final String UPTIME = "up_time";

    //=======================接收到的通知=================================
    public static final String TYPE ="type";
    public static final String EXTRA_NOTICE ="medicine_notice";
    public static final String EXTRA_CONTACTS="contacts_update";
    public static final String EXTRA_SETTING_DATA="setting_date_update";
    public static final String EXTRA_LOW_POWER="low_power";
    public static final String EXTRA_HEART_RATE="heart_rate";
    public static final String EXTRA_WATCH_LOCATION="watch_location_update";
    public static final String EXTRA_FENCE="fence_notice";
    public static final String EXTRA_WIFI_REQUEST="wifi_request";
    public static final String EXTRA_WIFI_RESULT="wifi_result";

    public static final String WIFI_SSID="ssid";
    public static final String WIFI_PWD="pwd";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    // 0 代表没有激活
    public static final String ACTIVE_NO = "active_number";
    public static final String HEART_RATE = "heart_rate_and_time";
    public static final String WATCH_ID = "watch_id";


    public static Uri uri_contact = Uri.parse("content://icc/adn");
    /**
     * 直接操作 sms 表的uri
     */
    public static Uri uri_sms = Uri.parse("content://sms");
}

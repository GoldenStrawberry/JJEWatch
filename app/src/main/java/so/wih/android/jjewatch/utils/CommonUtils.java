package so.wih.android.jjewatch.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ==============================================
 * Created by HuWei on 2016/12/8.
 * 普通的工具类
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class CommonUtils {
    /**
     * 格式化时间
     * @param Time
     * @return
     */
    public static String FormatTime(long Time) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return sdf.format(Time);
    }
    /**
     * 获取当前线程的名字
     * @param context
     * @return
     */
    @Nullable
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 手机号正则表达式
     **/
    public final static String MOBLIE_PHONE_PATTERN = "^((13[0-9])|(15[0-9])|(18[0-9])|(14[7])|(17[0|6|7|8]))\\d{8}$";

    /**
     * 通过正则验证是否是合法手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean isMobile(String phoneNumber) {
        Pattern p = Pattern.compile(MOBLIE_PHONE_PATTERN);
        Matcher m = p.matcher(phoneNumber);
        return m.matches();
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}

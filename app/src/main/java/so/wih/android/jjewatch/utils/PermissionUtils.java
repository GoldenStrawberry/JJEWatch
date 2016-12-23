package so.wih.android.jjewatch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * ==============================================
 * Created by HuWei on 2016/12/9.
 * 由于手机是6.0的，调试时用到了敏感权限
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class PermissionUtils {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 111;

    public static Boolean applyPermission(Context ctx){
        //检查是否需要申请
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return false; //不需要
        }else{
            //DENIED就需要进行申请授权
            return true ;
        }
    }

    public static void grantPermission(Activity ctx,String[] arrStr) {
        ActivityCompat.requestPermissions(ctx, arrStr, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }
}

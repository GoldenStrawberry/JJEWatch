package so.wih.android.jjewatch.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/25.
 */

public class MyToast {

    private static Toast toast;

    /**
     * 吐司
     * @param context
     * @param msg
     */
    public static void showToast(Context context,String msg){
        if(toast == null ){ //吐司消失了，创建消息
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg); //没消失，设置消息
        toast.show(); //显示吐司
    }
}

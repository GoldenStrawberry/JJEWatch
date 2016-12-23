package so.wih.android.jjewatch.utils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/2.
 */

public class OkHttpHelpter {

    private final String url;
    private OkHttpClient okHttpClient;
    public OkHttpHelpter(String url){
        okHttpClient = new OkHttpClient();
        this.url = url ;
    }
    public String connectRongCloud(String nonce, String timestamp, String sha1Str,String phone,String name){
        RequestBody body = new FormBody.Builder()
                .add("userId", phone)// 构造请求的参数
                .add("name", name)// 构造请求的参数
                .add("portraitUri","null")
                .build();
        //创建请求对象
        Request request = new Request.Builder()
                .addHeader("App-Key","pkfcgjstpw1u8")
                .addHeader("Nonce",nonce)
                .addHeader("Timestamp",timestamp)
                .addHeader("Signature",sha1Str)
                .post(body)
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();//同步任务
            if (response.isSuccessful()){ // 200
                return  response.body().string(); // 返回json数据
            }else{
                return "联网失败！";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage() ;
        }
    }
}

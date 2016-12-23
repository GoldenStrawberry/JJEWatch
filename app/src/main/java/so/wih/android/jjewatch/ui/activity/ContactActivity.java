package so.wih.android.jjewatch.ui.activity;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.bean.TokenBean;
import so.wih.android.jjewatch.utils.SHA1Utils;

/**
 * Created by HuWei on 2016/11/22.;
 * 登录注册界面 手机号 昵称 密码等
 */

public class ContactActivity extends BaseActivity {

    private static final String TAG = "huwei";

    @Override
    public void init() {
        super.init();

    }

    @Override
    public void initLayout() {
        super.initLayout();
        setContentView(R.layout.contact_content);
        //获取Token,成功会跳转到联系人界面
        getToken1();
    }

    @Override
    public void initView() {
        super.initView();

    }
    /**
     * 获取token
     */
    private void getToken1() {
        //获取sha1
        Random random=new Random();
        String nonce = String.valueOf(random.nextInt());
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String str = "q9MsOvAOyPvc"+nonce+timestamp;
        //获取Signature (数据签名)
        String sha1Str = SHA1Utils.getSHA1Str(str);
        OkHttpClient client=new OkHttpClient();
        String URL_POST = "http://api.cn.ronghub.com/user/getToken.json";
        RequestBody body = new FormBody.Builder()
                .add("userId", "18626255712")// 构造请求的参数
                .add("name", "lisi")// 构造请求的参数
                .add("portraitUri","null")
                .build();
        Request post_request = new Request.Builder()
                .url(URL_POST)// 指定请求的地址
                .addHeader("App-Key","pkfcgjstpw1u8")
                .addHeader("Nonce",nonce)
                .addHeader("Timestamp",timestamp)
                .addHeader("Signature",sha1Str)
                .post(body)// 指定请求的方式为POST
                .build();
        client.newCall(post_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败的处理
                Log.d(TAG, "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {	// 请求成功的处理
                ResponseBody body = response.body();
                String string = body.string();// 把返回的结果转换为String类型
                Log.d(TAG, "onResponse: "+string);
                try {
                    TokenBean tokenBean = new TokenBean();
                    JSONObject jsonObj = new JSONObject(string);
                    String token = jsonObj.getString("token");
                    String userId = jsonObj.getString("userId");
                    tokenBean.setToken(token);
                    tokenBean.setUserId(userId);
                    Log.d(TAG, "json : "+token);
                    getConnect(token,tokenBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void getConnect(String token, final TokenBean tokenBean) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSuccess: 成功"+s);
                Intent intent = new Intent(ContactActivity.this, MyChat.class);
                intent.putExtra("tokenbean",tokenBean);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.d(TAG, "onError: 失败"+errorCode);
            }

            @Override
            public void onTokenIncorrect() {
                Log.d(TAG, "onTokenIncorrect: token失效");
            }
        });
    }

}

package so.wih.android.jjewatch.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.util.ContactManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.myinterface.MyCallBack;
import so.wih.android.jjewatch.realm.MyFriends;
import so.wih.android.jjewatch.realm.RealmHelper;
import so.wih.android.jjewatch.utils.CommonUtils;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LogUtils;
import so.wih.android.jjewatch.utils.MyToast;
import so.wih.android.jjewatch.utils.NotificationUtils;

/**
 * ==============================================
 * Created by HuWei on 2017/1/13.
 * 语音查询天气界面、打电话、发短信、社保文件
 *
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class VoiceSearchActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.iv_mac)
    ImageView ivMac;
    @BindView(R.id.tv_voice)
    TextView tvVoice;
    @BindView(R.id.tv_1)
    TextView tv1;
    private SpeechUnderstander mSpeechUnderstander;
    private Realm realm;
    private SpeechRecognizer mIat;
    private LexiconListener mLexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String s, SpeechError speechError) {
            if (speechError != null) {
                LogUtils.logeHu("onLexiconUpdated: " + speechError.toString());
            } else {
                LogUtils.logdHu("onLexiconUpdated: 上传成功");
            }
        }
    };
    /**
     * 获取联系人监听器。
     */
    private ContactManager.ContactListener mContactListener = new ContactManager.ContactListener() {
        @Override
        public void onContactQueryFinish(String contactInfos, boolean changeFlag) {
            // 上传联系人
            runOnUiThread(new Runnable() {
                public void run() {
                    String str = null;
                    // 报java.lang.IllegalStateException:
                    // This Realm instance has already been closed, making it unusable.
                    // 打个补丁
                    if (realm != null) {
                        //上传服务器上的联系人+SIM卡联系人
                        allMsg = RealmHelper.getAllMsg(realm);
                        simFriends = CommonUtils.SimQuery(VoiceSearchActivity.this);

                        for (int i = 0; i < allMsg.size()+ simFriends.size(); i++) {
                            String data = "";
                            if(i< allMsg.size()){
                                data = allMsg.get(i).getName();
                            }else{
                                data = simFriends.get(i- allMsg.size()).getName();
                            }
                            str += (data + "\n");
                        }
                    }
                    if (str != null) {
                        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
                        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
                        int ret = mIat.updateLexicon("contact", str, mLexiconListener);
                        if (ret != ErrorCode.SUCCESS) {
                            //失败
                            LogUtils.logeHu("上传联系人失败！");
                        }else {
                            LogUtils.logdHu("上传联系人SUCCESS");
                        }
                    }
                }
            });
        }
    };
    private List<MyFriends> simFriends;
    private RealmResults<MyFriends> allMsg;

    @Override
    public void init() {
        super.init();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_voice_fun;
    }

    @Override
    public void initData() {
        //申请运行时权限
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(VoiceSearchActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }else{

            //上传联系人
            ContactManager mgr = ContactManager.createManager(this, mContactListener);
            mgr.asyncQueryAllContactsName();//同步联系人，联系人列表通过监听器回调的方式返回回来
        }
        //创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(context, null);//语音听写
        //设置听写参数
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

        //1.创建文本语义理解对象
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, null);
        //2.设置参数
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
        //即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "2000");
        // 设置标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");
    }

    @Override
    public void initListener() {
        ivMac.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mac:
                doAnimation(ivMac);
                tv1.setVisibility(View.GONE);
                tvVoice.setVisibility(View.VISIBLE);
                //申请运行时权限
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(VoiceSearchActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            Constants.MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                }else{
                    //3.开始语义理解
                    if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
                        mSpeechUnderstander.stopUnderstanding();
                    } else {
                        mSpeechUnderstander.startUnderstanding(mUnderstanderListener);
                    }
                }
                break;
        }
    }

    private void doAnimation(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1,0.9f,1,0.9f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(300);
        view.startAnimation(scaleAnimation);
    }

    private SpeechUnderstanderListener mUnderstanderListener = new SpeechUnderstanderListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {//音量值0~30
            MyToast.showToast(context, "音量大小:" + volume);
        }

        @Override
        public void onBeginOfSpeech() {//开始录音

        }

        @Override
        public void onEndOfSpeech() {//结束录音

        }

        @Override
        public void onResult(UnderstanderResult understanderResult) {
            String text = understanderResult.getResultString();
            if (!TextUtils.isEmpty(text)) {
                //将系统声音调至最大
                NotificationUtils.setVolume(context);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String text1 = jsonObject.getString("text");
                    String operate = jsonObject.getString("operation");
                    String serve = jsonObject.getString("service");
                    Log.d(Constants.TAG, "onResult: "+text1);
                    //解析内容
                    analyzeContent(jsonObject, text1, operate, serve);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
//            speechError.getPlainDescription(true)
            tvVoice.setText(speechError.getErrorDescription());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {//扩展用接口

        }
    };

    /**
     * 解析内容
     * @param jsonObject
     * @param text1 自己说的话
     * @param operate 操作类型
     * @param serve  哪个服务
     * @throws JSONException
     */
    private void analyzeContent(JSONObject jsonObject, String text1, String operate, String serve) throws JSONException {
        if("ANSWER".equals(operate)){
            JSONObject answer = jsonObject.getJSONObject("answer");
            String text2 = answer.getString("text");
            LogUtils.logdHu("回答："+text2);
            tvVoice.setText(text2);
            CommonUtils.SpeakerText(context, text2, new MyCallBack() {
                @Override
                public void sucess() {
                }

                @Override
                public void failed() {
                }
            });
        }else {                       //操作类型
            if("weather".equals(serve)){                          //天气
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray result = data.getJSONArray("result");
                JSONObject temp = new JSONObject(result.getString(0));
                String city = temp.getString("city");
                String airQuality = temp.getString("airQuality");
                String pm25 = temp.getString("pm25");
                String tempRange = temp.getString("tempRange");
                String weather = temp.getString("weather");
                String wind = temp.getString("wind");
                String haha = "今天" + city + "空气质量" + airQuality + ",天气" + weather + ",温度" + tempRange + ",pm2.5为" + pm25 + ",风向" + wind;
                tvVoice.setText(haha);
                //合成语音
                CommonUtils.SpeakerText(context,haha,new MyCallBack() {
                    @Override
                    public void sucess() { //播报完成
                    }

                    @Override
                    public void failed() {
                    }
                });
            }else if("telephone".equals(serve)){
                tvVoice.setText(text1);
                try {
                    String[] strings = text1.split("给");
                    String name = strings[1];
                    //防止服务器上没有的名字报错
                    MyFriends friends = RealmHelper.realmQueryPhone(realm, name);
                    String phone = "";
                    if(friends == null){
                        for (int i = 0; i < simFriends.size(); i++) {
                            String fName = simFriends.get(i).getName();
                            if(name.matches(fName)){
                                phone = simFriends.get(i).getPhone();
                            }
                        }
                    }else{
                        phone = friends.getPhone();
                    }
                    CommonUtils.getCallConnection(VoiceSearchActivity.this, phone);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if("message".equals(serve)){
                tvVoice.setText(text1);
                try {
                    String[] strings = text1.split("给");
                    String name = strings[1];
                    //防止服务器上没有的名字报错
                    MyFriends friends = RealmHelper.realmQueryPhone(realm, name);
                    String phone = "";
                    if(friends == null){
                        for (int i = 0; i < simFriends.size(); i++) {
                            String fName = simFriends.get(i).getName();
                            if(name.matches(fName)){
                                phone = simFriends.get(i).getPhone();
                            }
                        }
                    }else{
                        phone = friends.getPhone();
                    }
                    Intent intent = new Intent(VoiceSearchActivity.this, MessageActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (text1.contains("社保")) {
                MyToast.showToast(context, "读社保文件");
            }else{
                tvVoice.setText("不识别命令:"+text1);
                LogUtils.logdHu("语音命令反馈:"+jsonObject.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        realm.close();
        // 退出时释放连接
        mSpeechUnderstander.cancel();
        mSpeechUnderstander.destroy();
//        if(mTextUnderstander.isUnderstanding())
//            mTextUnderstander.cancel();
//        mTextUnderstander.destroy();
        super.onDestroy();

    }
}

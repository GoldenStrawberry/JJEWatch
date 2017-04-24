package so.wih.android.jjewatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import so.wih.android.jjewatch.api.util.RetrofitUtil;
import so.wih.android.jjewatch.bean.DataResult;
import so.wih.android.jjewatch.bean.Response;
import so.wih.android.jjewatch.bean.WatchInfo;
import so.wih.android.jjewatch.myinterface.MyCallBack;
import so.wih.android.jjewatch.realm.MyFriends;
import so.wih.android.jjewatch.realm.RealmHelper;

/**
 * 连接服务器的工具类
 * Created by HuWei on 2017/1/10.
 */

public class ServerHelpter {
    public static String code = "60:d3:c8:0b:9e:3a";
    public static String qrCode = CommonUtils.getQRCode(CommonUtils.getMac(), Constants.WATCH_DEVICE);
    /**
     * 获取手表信息
     */
    public static void getWatchInfo(final SharedPreferences sp) {

        RetrofitUtil.getWatchAPI().getWatchInfo(qrCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<WatchInfo>>() {
                    @Override
                    public void call(Response<WatchInfo> watchInfoResponse) {
                        String message = watchInfoResponse.message;
                        if("success".equals(message)){
                            List<WatchInfo.DataBean> dataResult = watchInfoResponse.result.getData();
                            if(dataResult.size()>0){
                                for (int i = 0; i < dataResult.size(); i++) {
                                    int watch_user_id = dataResult.get(i).getWatch_user_id();
                                    int wid = dataResult.get(i).getId();
                                    //获取电话号码
                                    String phone = dataResult.get(i).getWatchUser().getPhone();
                                    int active = dataResult.get(i).getActive();
                                    String password = "123456";
                                    //用来注册极光账号
                                    registerJMessage(phone, password);

                                    sp.edit().putInt(Constants.WATCH_USER_ID,watch_user_id).apply();
                                    sp.edit().putInt(Constants.WATCH_ID,wid).apply();
                                    sp.edit().putString(Constants.LOGIN_PHONE,phone).apply();
                                    sp.edit().putInt(Constants.ACTIVE_NO,active).apply();
                                    String latitude = sp.getString(Constants.LATITUDE, "");
                                    String longitude = sp.getString(Constants.LONGITUDE, "");
                                    int battery_state = sp.getInt(Constants.BATTERY, 0);
                                    ServerHelpter.watchUploadLocation(latitude,longitude,battery_state,watch_user_id);
                                }
                            }else{
                                LogUtils.logeHu("数据为0");
                            }
                        }else {
                            LogUtils.logeHu("code 200，操作错误");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("获取手表信息:"+throwable.getMessage());
                    }
                });
    }
    private static void registerJMessage(final String username, final String password) {
        JMessageClient.register(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                LogUtils.logdHu(i+s);
                if(i==0||i==898001){
                    //登录
                    loginJMessage(username,password);
                }
            }
        });
    }



    private static void loginJMessage(String username, String password) {
        JMessageClient.login(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if(i == 0){ //登陆成功
                    LogUtils.logdHu("登陆成功！"+s);
                }else{
                    LogUtils.logeHu(i+"登录失败！"+s);
                }
            }
        });
    }

    /**
     * 请求所有联系人
     */
    public static void getAllWatchContacts(final Realm realm , int wuid) {
        RetrofitUtil.getWatchAPI().watchGetAllContacts(wuid,qrCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<DataResult<MyFriends>>>() {
                    @Override
                    public void call(Response<DataResult<MyFriends>> dataResultResponse) {
                        String message = dataResultResponse.message;
                        if("success".equals(message)){
                            List<MyFriends> dataResult = dataResultResponse.result.data;
                            RealmHelper.realmDeleteAll(realm);
                            for (int i = 0; i < dataResult.size(); i++) {
                                MyFriends friends = dataResult.get(i);
                                //添加到数据库
                                RealmHelper.realmInsert(realm,friends);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("获取好友"+throwable.getMessage());
                    }
                });
    }
    /**
     * 添加好友
     */
    public static void watchAddFriend(final Context context ,final Realm realm ,int wuid , int fwuid){
        RetrofitUtil.getWatchAPI().watchAddFriend(wuid,qrCode,fwuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<DataResult<MyFriends>>>() {
                    @Override
                    public void call(Response<DataResult<MyFriends>> dataResultResponse) {
                        String message = dataResultResponse.message;
                        if("success".equals(message)){
                            MyToast.showToast(context,"添加好友成功！");
                            List<MyFriends> dataResult = dataResultResponse.result.data;
                            for (int i = 0; i < dataResult.size(); i++) {
                                MyFriends friends = dataResult.get(i);
                                //添加到数据库
                                RealmHelper.realmInsert(realm,friends);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("添加好友:" + throwable.getMessage());
                    }
                });
    }
    /**
     * wuid
     * cid
     * 手表删除好友
     */
    public static void watchDeleteFriend(final Context context , int wuid , int cid){
        RetrofitUtil.getWatchAPI().watchDeleteFriend(wuid,cid,qrCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        String message = response.message;
                        if("success".equals(message)){
                            MyToast.showToast(context,"删除好友成功");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("手表删除好友:"+throwable.getMessage());
                    }
                });

    }

    /**
     * 上传位置和电量信息
     */
    public static void watchUploadLocation(final String lat, final String lon, final int power , int wuid){
        RetrofitUtil.getWatchAPI().watchUploadLocation(lat,lon,power,wuid,qrCode,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        String message = response.message;
                        if("success".equals(message)){
                            LogUtils.logdHu("上传成功！"+lat+lon+power);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logdHu("上传经纬度:"+throwable.getMessage());
                    }
                });
    }

    /**
     * 激活手表在服务器的定时功能
     * @param sp
     */
    public static void watchActiveTimer(final SharedPreferences sp){
        RetrofitUtil.getWatchAPI().activeWatch(
                sp.getInt(Constants.WATCH_ID, 0),qrCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Object>>() {
                    @Override
                    public void call(Response<Object> objectResponse) {
                        LogUtils.logdHu("设置别名"+objectResponse.message);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("设置别名"+throwable.getMessage());
                    }
                });
    }

    /**
     * 上传心率
     * @param value
     * @param time
     * @param wuid
     */
    public static void upLoadBpm(int value, String time , int wuid , final MyCallBack myCallBack){
        RetrofitUtil.getWatchAPI().uploadBps(value,time,wuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Object>>() {
                    @Override
                    public void call(Response<Object> objectResponse) {
                        LogUtils.logdHu("上传服务器心率"+objectResponse.message);
                        myCallBack.sucess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("上传服务器心率"+throwable.getMessage());
                        myCallBack.failed();
                    }
                });
    }

    /**
     * 上传步数
     * @param value
     * @param time
     * @param wuid
     */
    public static void upLoadStep(int value,String time , int wuid){
        RetrofitUtil.getWatchAPI().uploadStep(value,time,wuid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<Object>>() {
                    @Override
                    public void call(Response<Object> objectResponse) {
                        LogUtils.logdHu("上传服务器步数"+objectResponse.message);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.logeHu("上传服务器步数"+throwable.getMessage());
                    }
                });
    }
}

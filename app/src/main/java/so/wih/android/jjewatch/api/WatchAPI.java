package so.wih.android.jjewatch.api;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import so.wih.android.jjewatch.bean.DataResult;
import so.wih.android.jjewatch.bean.DeviceDidBean;
import so.wih.android.jjewatch.bean.Response;
import so.wih.android.jjewatch.bean.WatchInfo;
import so.wih.android.jjewatch.bean.WatchResult;
import so.wih.android.jjewatch.bean.WeatherBean;
import so.wih.android.jjewatch.realm.MyFriends;

/**
 * Created by king on 2016/12/28.
 */

public interface WatchAPI {
    /*
     * 手表端根据did获取信息
     */
    @GET("watches")
    Observable<Response<WatchInfo>> getWatchInfo(@Query("did") String did);

    /**
     * 手表获取联系人
     * @param wuid watch_user_id
     * @param did mac
     * @return
     */
    @GET("wusers/{wuid}/contacts")
    Observable<Response<DataResult<MyFriends>>> watchGetAllContacts(@Path("wuid") int wuid ,@Query("did") String did);

    /**
     * 手表添加好友
     */
    @POST("wusers/{wuid}/contacts")
    Observable<Response<DataResult<MyFriends>>> watchAddFriend(@Path("wuid") int wuid ,@Query("did") String did
                                        ,@Query("fwuid") int fwuid);

    /**
     *手表删除好友
     * @param wuid
     * @param cid
     * @param did
     * @return
     */
    @DELETE("wusers/{wuid}/contacts/{cid}")
    Observable<Response> watchDeleteFriend(@Path("wuid") int wuid ,@Path("cid") int cid
                                        ,@Query("did") String did);

    /**
     * 手表上传足迹
     * @param lat
     * @param lon
     * @param power
     * @param wuid
     * @param did
     * @return
     */
    @POST("footmarks")
    @FormUrlEncoded
    Observable<Response> watchUploadLocation(@Field("lat") String lat,@Field("lon") String lon
                                            ,@Field("power") int power,@Field("wuid") int wuid
                                            ,@Field("did") String did,@Field("type") int type);


//==============================================================================================================================
    //http://api.map.baidu.com/telematics/v3/weather?location=%E8%8B%8F%E5%B7%9E&output=json&ak=6tYzTvGZSOpYB5Oc2YGGOKt8
    //location=苏州&output=json&ak=6tYzTvGZSOpYB5Oc2YGGOKt8
    @GET("telematics/v3/weather")
    Observable<retrofit2.Response<WeatherBean>> getWeather(@Query("location") String city,
                                                           @Query("output") String str,
                                                           @Query("ak") String ak);

    /**
     * @param wdid 设备识别码
     * @return
     */
    @GET("watch/device/watch")
    Observable<retrofit2.Response<DeviceDidBean>> watchGetPhoneById(@Query("wdid") String wdid);




    @POST("watch/user/{wuser_oid}/wfriend")
    @FormUrlEncoded
    Observable<WatchResult<DataResult<MyFriends>>> watchAddFriend(@Path("wuser_oid") long wuser_oid,
                                                         @Field("fwuser_oid") long fwuser_oid);

    //获取手表用户下所有联系人/users/{uid}/wusers/{wuid}/contacts     String authorize
    @GET("watch/user/{wuser_oid}/wfriends")
    Observable<WatchResult<DataResult<MyFriends>>>  watchGetFriend(@Path("wuser_oid") long wuser_oid);



    @DELETE("watch/user/{wuser_oid}/wfriend")
    Observable<WatchResult<Object>> watchDelFriend(@Path("wuser_oid") long wuser_oid,
                                                @Query("fwuser_oid") long fwuser_oid);



    @POST("watch/footmark")
    @FormUrlEncoded
    Observable<WatchResult<Object>> watchUploadFootmark(
            @Field("wuser_oid") long wuser_oid,
            @Field("lat") String lat,
            @Field("lon") String lon);

    //==========================================================================================


    /**
     * 上传当天步数
     * @param
     * @param count 步数
     * @return
     */
    @POST("stepcount")
    @FormUrlEncoded
    Observable<Response<Object>> uploadStep(@Field("count") int count,@Field("time") String time,@Field("wuid") int wuid);



    /**
     * 上传心率数据
     * @return
     */
    @POST("heartrate")
    @FormUrlEncoded
    Observable<Response<Object>> uploadBps(
            @Field("bpm") int bpm,
            @Field("time") String time,
            @Field("wuid") int wuid);


    @PUT("watches/{wid}")
    Observable<Response<Object>> activeWatch(@Path("wid") int wid,@Query("did") String did);
    /**
     * 手表获取wifi
     * @param lat
     * @param lon
     * @param power
     * @param wuid
     * @param did
     * @return
     */
    @POST("footmarks")
    @FormUrlEncoded
    Observable<Response> watchGetWifi(@Field("lat") String lat,@Field("lon") String lon
            ,@Field("power") int power,@Field("wuid") int wuid
            ,@Field("did") String did,@Field("type") int type);

}

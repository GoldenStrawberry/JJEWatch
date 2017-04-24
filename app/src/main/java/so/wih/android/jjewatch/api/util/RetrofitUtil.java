package so.wih.android.jjewatch.api.util;


import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import so.wih.android.jjewatch.api.WatchAPI;
import so.wih.android.jjewatch.bean.WeatherBean;
import so.wih.android.jjewatch.bean.WeatherDatum;
import so.wih.android.jjewatch.bean.WeatherResult;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.LogUtils;

/**
 * Created by king on 2016/11/17.
 */

public class RetrofitUtil {
    private static final String BASE_URL = "http://192.168.1.102:8080/jwatch/";
    private static final String WATCH_BASE_URL_2 = "http://115.28.12.182:8080/beehive/rest/";
    private static final String WATCH_BASE_URL = "http://115.28.12.182:8080/jwatch/";
    private static OkHttpClient mOkHttpClient;
    static {
        initOkHttpClient();
    }

    public static WatchAPI getWatchAPI(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(WATCH_BASE_URL)
                .client(mOkHttpClient)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WatchAPI.class);
    }
    /**
     * 初始化OKHttpClient
     */
    private static void initOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitUtil.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
//                    Cache cache = new Cache(new File(MyApplication.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

                    mOkHttpClient = new OkHttpClient.Builder()
//                            .cache(cache)
//                            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("218.56.132.155", 8080)))
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    public static void getWeather(final SharedPreferences sp , String city,
                                  final TextView tv_pm25,final TextView tv_weather){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.map.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        WatchAPI watchAPI = retrofit.create(WatchAPI.class);
        final Observable<Response<WeatherBean>> weather = watchAPI.getWeather(city, "json", "6tYzTvGZSOpYB5Oc2YGGOKt8");
        Observable<Response<WeatherBean>> responseObservable = weather.subscribeOn(Schedulers.io());
        responseObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<WeatherBean>>() {
                    @Override
                    public void call(Response<WeatherBean> weatherBeanResponse) {
                        String message = weatherBeanResponse.message();
                        if("OK".equals(message)){
                            WeatherBean body = weatherBeanResponse.body();
                            String date = body.getDate();//2017-01-09
                            sp.edit().putString(Constants.WEATHER_DATE,date).apply();
                            List<WeatherResult> weatherResults = body.getWeatherResults();
                            String pm25 = weatherResults.get(0).getPm25();//155
                            sp.edit().putString(Constants.WEATHER_PM25,pm25).apply();
                            tv_pm25.setText("pm25  "+pm25);
                            List<WeatherDatum> weatherData = weatherResults.get(0).getWeatherData();
                            if(weatherData == null){
                                Log.e(Constants.TAG, "getWeather1: weatherData=null");
                            }else{
                                String weather1 = weatherData.get(0).getWeather();
                                sp.edit().putString(Constants.WEATHER_WEATHER1,weather1).apply();
                                String temperature = weatherData.get(0).getTemperature();
                                tv_weather.setText(weather1+temperature);
                                sp.edit().putString(Constants.WEATHER_TEMPERA,temperature).apply();
                            }
                        }else{
                            LogUtils.logeHu("获取天气200，操作错误！");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        String message = throwable.getMessage();
                        LogUtils.logeHu("获取天气:"+message);
                    }
                });
    }
}

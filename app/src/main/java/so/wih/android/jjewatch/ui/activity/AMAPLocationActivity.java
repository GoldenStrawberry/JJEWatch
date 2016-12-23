package so.wih.android.jjewatch.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import io.rong.message.LocationMessage;
import so.wih.android.jjewatch.AppContexts;
import so.wih.android.jjewatch.MyApplication;
import so.wih.android.jjewatch.R;
import so.wih.android.jjewatch.utils.Constants;
import so.wih.android.jjewatch.utils.MyToast;

import static so.wih.android.jjewatch.R.id.btn_cancel;


/**
 * 地图页面，定位
 * Created by HuWei on 2016/12/6.
 */

public class AMAPLocationActivity extends AppCompatActivity implements View.OnClickListener,
        LocationSource, GeocodeSearch.OnGeocodeSearchListener, AMapLocationListener, AMap.OnCameraChangeListener {
    private MapView mapView;
    private AMap aMap;
//    private LocationManagerProxy mLocationManagerProxy;
    private Handler handler = new Handler();
    private LocationSource.OnLocationChangedListener listener;
    private LatLng myLocation = null;
    private Marker centerMarker;
    private boolean isMovingMarker = false;
    private BitmapDescriptor successDescripter;
    private GeocodeSearch geocodeSearch;
    private LocationMessage mMsg;
    private TextView tvCurLocation;
    private boolean model = false;
    private boolean isPerview;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private Button btn_send;
    private Button cancel_btn;
    private AMapLocation mapLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
//        getSupportActionBar().setTitle("地理位置");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.de_actionbar_back);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);


        initUI();
        initAmap();
        setUpLocationStyle();
    }

    private void initAmap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        if (getIntent().hasExtra("location")) {
            Log.d(Constants.TAG, "initAmap: "+getIntent().hasExtra("location"));
            isPerview = true;
            mMsg = getIntent().getParcelableExtra("location");
            tvCurLocation.setVisibility(View.GONE);
            returns.setVisibility(View.GONE);

            if (model) {  //false
                CameraPosition location = new CameraPosition.Builder()
                        .target(new LatLng(mMsg.getLat(), mMsg.getLng())).zoom(18).bearing(0).tilt(30).build();
                show(location);
            } else {
                aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(mMsg.getLat(), mMsg.getLng())).title(mMsg.getPoi())
                        .snippet(mMsg.getLat() + "," + mMsg.getLng()).draggable(false));
                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(mMsg.getLat(), mMsg.getLng())).zoom(16).bearing(0).tilt(30).build()));
            }
            return;
        }
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(15);//设置缩放监听
        aMap.moveCamera(cameraUpdate);
        //初始化定位
        mLocationClient =  new AMapLocationClient(MyApplication.getCtx());

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

//        LocationSource.OnLocationChangedListener


        successDescripter = BitmapDescriptorFactory.fromResource(R.mipmap.icon_usecarnow_position_succeed);
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

    }

    private static final String MAP_FRAGMENT_TAG = "map";
    private SupportMapFragment aMapFragment;


    private void show(CameraPosition location) {
        AMapOptions aOptions = new AMapOptions();
        aOptions.zoomGesturesEnabled(true);
        aOptions.scrollGesturesEnabled(false);

        aOptions.camera(location);

        if (aMapFragment == null) {
            aMapFragment = SupportMapFragment.newInstance(aOptions);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.add(android.R.id.content, aMapFragment,
                    MAP_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    private ImageView returns;

    private void initUI() {
        returns = (ImageView) findViewById(R.id.myLocation);
        returns.setOnClickListener(this);
        btn_send = (Button) findViewById(R.id.btn_send);
        cancel_btn = (Button) findViewById(R.id.btn_cancel);
        btn_send.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
        tvCurLocation = (TextView) findViewById(R.id.location);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myLocation:
                CameraUpdate update = CameraUpdateFactory.changeLatLng(myLocation);
                aMap.animateCamera(update);
                break;
            case R.id.btn_send:

                if (mMsg != null) {
                    AppContexts.getInstance().getLastLocationCallback().onSuccess(mMsg);
                    AppContexts.getInstance().getInstance().setLastLocationCallback(null);
                    finish();
                } else {
                    AppContexts.getInstance().getInstance().getLastLocationCallback()
                            .onFailure("定位失败");
                }
                break;
            case btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    //激活位置接口。
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
//        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
//        mLocationManagerProxy.requestLocationData(
//                LocationProviderProxy.AMapNetwork, -1, 100, this);

    }

    @Override
    public void deactivate() {
        //处理定位更新的接口。
//        mLocationClient.stopLocation();
//        onLocationChanged(mMsg);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        if (rCode == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
                endAnim();

                centerMarker.setIcon(successDescripter);
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                String shortAdd = formatAddress.replace(regeocodeAddress.getProvince(), "").replace(regeocodeAddress.getCity(), "").replace(regeocodeAddress.getDistrict(), "");
                tvCurLocation.setText(shortAdd);
                double latitude = regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
                double longitude = regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();

                mMsg = LocationMessage.obtain(latitude, longitude, shortAdd, getMapUrl(latitude, longitude));
            } else {
                MyToast.showToast(AMAPLocationActivity.this,"没有搜索到结果");
            }
        } else {
            MyToast.showToast(AMAPLocationActivity.this,"搜索失败,请检查网络"+regeocodeResult.getRegeocodeAddress());
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
//        this.mapLocation = aMapLocation ;
        //获取地理消息
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            if (listener != null) {
                listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            }
//            aMap.setOnCameraChangeListener(AMAPLocationActivity.this);
            myLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取当前位置经纬度
            tvCurLocation.setText(aMapLocation.getStreet() + aMapLocation.getPoiName());//当前位置信息

            double latitude = aMapLocation.getLatitude();
            double longitude = aMapLocation.getLongitude();
            mMsg = LocationMessage.obtain(latitude, longitude, aMapLocation.getStreet() + aMapLocation.getPoiName(), getMapUrl(latitude, longitude));
            addChooseMarker();
        }else{
            Log.d("huwei", "onLocationChanged: "+aMapLocation.getErrorInfo()+"错误代码"+aMapLocation.getErrorCode());
        }
    }


    private void addChooseMarker() {
        //加入自定义标签
        MarkerOptions centerMarkerOption = new MarkerOptions().position(myLocation).icon(successDescripter);
        centerMarker = aMap.addMarker(centerMarkerOption);
        centerMarker.setPositionByPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);

        CameraUpdate update = CameraUpdateFactory.zoomTo(17f);
        aMap.animateCamera(update, 1, new AMap.CancelableCallback() {
            @Override
            public void onFinish() {
                aMap.setOnCameraChangeListener(AMAPLocationActivity.this);
            }

            @Override
            public void onCancel() {
            }
        });


    }

    private void setMovingMarker() {
        if (!isMovingMarker)
            return;

        isMovingMarker = true;
        centerMarker.setIcon(successDescripter);
        hideLocationView();
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (centerMarker != null) {
            setMovingMarker();
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLonPoint point = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);
        if (centerMarker != null) {
            animMarker();
        }
        showLocationView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    private ValueAnimator animator = null;

    private void animMarker() {
        isMovingMarker = false;
        if (animator != null) {
            animator.start();
            return;
        }
        animator = ValueAnimator.ofFloat(mapView.getHeight() / 2, mapView.getHeight() / 2 - 30);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(150);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                centerMarker.setPositionByPixels(mapView.getWidth() / 2, Math.round(value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                centerMarker.setIcon(successDescripter);
            }
        });
        animator.start();
    }

    private void endAnim() {
        if (animator != null && animator.isRunning())
            animator.end();
    }

    private void hideLocationView() {
        ObjectAnimator animLocation = ObjectAnimator.ofFloat(tvCurLocation, "TranslationY", -tvCurLocation.getHeight() * 2);
        animLocation.setDuration(200);
        animLocation.start();
    }

    private void showLocationView() {
        ObjectAnimator animLocation = ObjectAnimator.ofFloat(tvCurLocation, "TranslationY", 0);
        animLocation.setDuration(200);
        animLocation.start();
    }

    private void setUpLocationStyle() {
        // 自定义系统定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.mipmap.img_location_now));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.strokeColor(R.color.main_theme_color);
        myLocationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private Uri getMapUrl(double x, double y) {
        String url = "http://restapi.amap.com/v3/staticmap?location=" + y + "," + x +
                "&zoom=17&scale=2&size=150*150&markers=mid,,A:" + y + ","
                + x + "&key=" + "ee95e52bf08006f63fd29bcfbcf21df0";
        return Uri.parse(url);
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.de_location_menu, menu);

        if (isPerview) {
            menu.getItem(0).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_location:
                mMsg = LocationMessage.obtain(105, 105, "", getMapUrl(105, 105));
                if (mMsg != null) {
                            AppContexts.getInstance().getLastLocationCallback().onSuccess(mMsg);
                            AppContexts.getInstance().getInstance().setLastLocationCallback(null);
                    finish();
                } else {
                    AppContexts.getInstance().getInstance().getLastLocationCallback()
                            .onFailure("定位失败");
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

}


package com.tencent.navigation.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.iflytek.tts.TtsHelper;
import com.tencent.tencentmap.navisdk.TencentNavigation;
import com.tencent.tencentmap.navisdk.callback.navi.IOverSpeedListener;
import com.tencent.tencentmap.navisdk.callback.navi.ISearchRouteCallback;
import com.tencent.tencentmap.navisdk.callback.navi.ITtsListener;
import com.tencent.tencentmap.navisdk.data.GpsLocation;
import com.tencent.tencentmap.navisdk.data.NaviRoute;
import com.tencent.tencentmap.navisdk.data.NaviTypeEnum;
import com.tencent.tencentmap.navisdk.data.OverSpeedInfo;
import com.tencent.tencentmap.navisdk.data.RouteSearchError;
import com.tencent.tencentmap.navisdk.data.TtsText;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航sdk接入demo
 *
 * @author selenali
 */
@SuppressLint("NewApi")
public class TencentNaviMapActivity extends FragmentActivity {

    /**
     * 地图对象
     */
    private TencentMap mMap;

    /**
     * 定位点提供实例
     */
    private GpsNavi gpsInfo = null;

    /**
     * 导航实例
     */
    private TencentNavigation tencentNaviManager = null;

    /**
     * 算路起点经纬度
     */
    private LatLng startLatlng = new LatLng(40.0393122, 116.2889099);

    /**
     * 算路起点sdk需要传入的结构
     */
    private GpsLocation startPosition = null;

    /**
     * 算路终点
     */
    private LatLng destLatlng = new LatLng(39.9608067, 116.220932);

    /**
     * 算路途经点
     */
    private LatLng passPoint = new LatLng(39.982633, 116.311963);

    /**
     * 算路终点marker
     */
    private Marker markerDestination = null;

    /**
     * 算路终点marker id
     */
    private String strDestiMarkerId = "";

    /**
     * 算路起点marker
     */
    private Marker markerStart = null;

    /**
     * 算路起点marker id
     */
    private String strStartMarkerId = "";

    /**
     * marker点击事件监听
     */
//    private Map.OnMarkerClickListener markerClickListener;

    /**
     * 记录的路线id
     */
    private int routeId;

    /**
     * 导航sdk播报回调 FIXME:这里语音播报模块也不在sdk里，以下回调拿科大讯飞播报为例
     */
    private ITtsListener ttsListener = new ITtsListener() {

        @Override
        public void initTts() {
            TtsHelper.getInstance().checkTTSFile(TencentNaviMapActivity.this);
        }

        @Override
        public void textToSpeech(TtsText ttsText) {
            com.iflytek.tts.TtsText readText = new com.iflytek.tts.TtsText();
            readText.text = ttsText.text;
            readText.assetPath = ttsText.path;
            TtsHelper.getInstance().read(readText, TencentNaviMapActivity.this);
        }
    };

    /**
     * 导航sdk超速回调
     */
    private IOverSpeedListener overSpeedListener = new IOverSpeedListener() {
        @Override
        public void OverSpeed(OverSpeedInfo info) {
            if (info == null) {
                return;
            }
            Toast.makeText(TencentNaviMapActivity.this, "速度限制："+info.limitSpeed +"\n"+"类型："+info.type, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化demo页面和导航实例
        setContentView(R.layout.simulate_navi_demo);

        SupportMapFragment fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = fragment.getMap();
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // 初始化导航实例
        initTencentNavigation();

        // 设置地图的点击监听，用于动态更改起终点
//        mMap.addOnMapClickListener(new Map.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng arg0) {
//                if (markerDestination != null) {
//                    mMap.remove(markerDestination);
//                    markerDestination = null;
//                }
//
//                markerDestination = mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromAsset(TencentNaviMapActivity.this, "tencentmap/navisdk/navi_icon_goal.png"))
//                        .position(arg0).anchor(0.5f, 1));
//
//                markerDestination.setDodgeInfoWindowEnabled(false);
//                strDestiMarkerId = markerDestination.getId();
//                markerDestination.setOnClickListener(markerClickListener);
//            }
//        });
//
//        // 设置地图的长按的点击监听，用于动态更改起终点
//        mMap.addOnMapLongClickListener(new Map.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng arg0) {
//                if (markerStart != null) {
//                    mMap.remove(markerStart);
//                    markerStart = null;
//                }
//                markerStart = mMap.addMarker(new MarkerOptions()
//                            .icon(BitmapDescriptorFactory.fromAsset(TencentNaviMapActivity.this, "tencentmap/navisdk/navi_icon_start.png"))
//                            .position(arg0).anchor(0.5f, 1));
//
//                markerStart.setDodgeInfoWindowEnabled(false);
//                strStartMarkerId = markerStart.getId();
//                markerStart.setOnClickListener(markerClickListener);
//            }
//        });
//
//        // 设置地图山marker的点击监听
//        markerClickListener = new Map.OnMarkerClickListener() {
//
//            @Override
//            public boolean onMarkerClick(Marker arg0) {
//                if (arg0 == null) {
//                    return true;
//                }
//                if (strDestiMarkerId.equals(arg0.getId()) == true) {
//                    LatLng dest = arg0.getPosition();
//                    destLatlng = dest;
//                    if (markerDestination != null) {
//                        mMap.remove(markerDestination);
//                        markerDestination = null;
//                    }
//                    return true;
//                }
//                if (strStartMarkerId.equals(arg0.getId()) == true) {
//                    LatLng start = arg0.getPosition();
//                    GpsLocation startPt = new GpsLocation();
//                    startPt.latitude = start.latitude;
//                    startPt.longitude = start.longitude;
//                    startPosition = startPt;
//                    if (markerStart != null) {
//                        mMap.remove(markerStart);
//                        markerStart = null;
//                    }
//                    return true;
//                }
//
//                return true;
//            }
//        };

        // 初始化menu
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        // 初始化步行按钮
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 切换步行或驾车算路，默认是驾车
//                tencentNaviManager.setNaviType(NaviTypeEnum.NAVI_TYPE_WALK);
//
//                tencentNaviManager.setNaviType(NaviTypeEnum.NAVI_TYPE_CAR);
            }
        });
    }

    /**
     * 初始化导航实例
     */
    private void initTencentNavigation() {
        // 初始化实例，并传入底图对象
        tencentNaviManager = new TencentNavigation(mMap.getMapView(), NaviTypeEnum.NAVI_TYPE_CAR);

        // 设置播报监听callback
        tencentNaviManager.setTtsListener(ttsListener);

        // 设置是否开启路口放大图功能
        tencentNaviManager.setCrossingEnlargePictureEnable(true);

        // 设置是否开启电子眼图片功能
        tencentNaviManager.setElectriEyesPictureEnable(true);

        // 设置导航overlay是否显示
        tencentNaviManager.setNavigationOverlayEnable(true);

        // 设置导航中超速的回调
        tencentNaviManager.setOverSpeedListener(overSpeedListener);

        // 初始化起点
        startPosition = new GpsLocation();
        startPosition.latitude = startLatlng.latitude;// 39.984130;
        startPosition.longitude = startLatlng.longitude;// 116.307501;
        startPosition.direction = 180;

        // 初始化定位点提供实例
        if (gpsInfo == null) {
            gpsInfo = new GpsNavi();
        }

        gpsInfo.enableGps(this);
        gpsInfo.setNavigationManager(tencentNaviManager);
    }

    /**
     * 创建菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 1, Menu.NONE, "设途经点"); //可选
        menu.add(Menu.NONE, 2, Menu.NONE, "算路");
        menu.add(Menu.NONE, 3, Menu.NONE, "开始导航");
        menu.add(Menu.NONE, 4, Menu.NONE, "停止导航");
        menu.add(Menu.NONE, 5, Menu.NONE, "开始模拟导航");
        menu.add(Menu.NONE, 6, Menu.NONE, "停止模拟导航");
        menu.add(Menu.NONE, 7, Menu.NONE, "添加路线");
        menu.add(Menu.NONE, 8, Menu.NONE, "移除路线");
        return true;
    }

    /**
     * 导航sdk算路回调
     */
    private ISearchRouteCallback searchRouteCallback = new ISearchRouteCallback(){

        @Override
        public void onBeginToSearch() {
            // 开始算路

        }

        @Override
        public void onFinishToSearch(ArrayList<NaviRoute> routs, RouteSearchError err) {
            // 结束算路
            // FIXME:若auto choose route设置为false 则需要调用setNaviRoute 传入route,之后才能开始导航
//            if(routs != null && routs.size() > 0){
//                tencentNaviManager.setNaviRoute(routs.get(0));
//            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                // 设置途经点
                List<LatLng> listPass = new ArrayList<LatLng>();
                listPass.add(passPoint);
                tencentNaviManager.setWayPoints(listPass);
                break;
            case 2:
                // auto choose route 算路后自动取路线 并在底图上进行绘制路线
                tencentNaviManager.setAutoChooseNaviRoute(true);
                tencentNaviManager.calculateRoute(startPosition, destLatlng, searchRouteCallback);
                break;
            case 3:
                // FIXME：若auto choose route设置为false 则需要调用setNaviRoute 传入route
                tencentNaviManager.startNavi();
                break;
            case 4:
                // 结束导航
                tencentNaviManager.stopNavi();
                break;
            case 5:
                // 开始模拟导航
                tencentNaviManager.simulateNavi();
                break;
            case 6:
                // 结束模拟导航
                tencentNaviManager.stopSimulateNavi();
                break;
            case 7:
                // 添加路线到地图上
                LatLng start = new LatLng(startPosition.latitude, startPosition.longitude);
                LatLng end = new LatLng(destLatlng.latitude, destLatlng.longitude);
                TencentNavigation.RouteSearchOptions opts = new TencentNavigation.RouteSearchOptions(start, end);
                opts.withTraffic = true;//是否需要路况
                routeId = tencentNaviManager.addRoute(opts);
                break;
            case 8:
                // 从地图上移除路线
                tencentNaviManager.removeRoute(routeId);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 销毁导航实例
     */
    @Override
    protected void onDestroy() {
        tencentNaviManager.stopSimulateNavi();
        tencentNaviManager.stopNavi();
        gpsInfo.disableGps();
        tencentNaviManager.onDestroy();

        super.onDestroy();
    }
}

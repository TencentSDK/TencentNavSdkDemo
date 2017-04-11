package com.tencent.navigation.demo;

import android.content.Context;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.navisdk.TencentNavigation;
import com.tencent.tencentmap.navisdk.callback.navi.ILocationChangedListener;
import com.tencent.tencentmap.navisdk.data.GpsLocation;

/**
 * 导航定位点提供管理类，提供给导航sdk必要的定位点
 * FIXME：导航sdk没有定位能力，需要外部传入定位点信息。该类举例使用腾讯的定位模块如何给导航sdk传点
 *
 * @author selenali
 */
public class GpsNavi implements TencentLocationListener {

    /**
     * 腾讯定位sdk定位request
     */
    private TencentLocationRequest request = TencentLocationRequest.create();

    /**
     * 腾讯定位sdk定位管理类
     */
    private TencentLocationManager locationManager = null;

    /**
     * 导航sdk实例
     */
    private TencentNavigation tencentNaviManger = null;

    /**
     * 导航sdk需要的定位回调
     */
    private ILocationChangedListener tencentGetLatestLocation = new ILocationChangedListener() {

        @Override
        public GpsLocation getLastKnownLocation() {
            // 将定位sdk获取到的定位点转换成导航sdk需要的定位点类型
            TencentLocation tencentLocation = locationManager.getLastKnownLocation();
            if (tencentLocation == null)
                return null;
            return convertToCommonLocation(tencentLocation);
        }

    };

    /**
     * 定位sdk开始工作
     */
    public void enableGps(Context context) {
        request.setInterval(1000);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_GEO);
        locationManager = TencentLocationManager.getInstance(context);
        locationManager.requestLocationUpdates(request, this);
    }

    /**
     * 定位sdk停止工作
     */
    public void disableGps() {
        if (locationManager == null) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    /**
     * 定位sdk回调
     */
    @Override
    public void onLocationChanged(TencentLocation locResult, int error,
                                  String arg2) {
        // 通知导航sdk定位点变化
        if (tencentNaviManger != null) {
            tencentNaviManger.onLocationChanged(convertToCommonLocation(locResult), error, arg2);
        }
    }

    /**
     * 定位sdk回调
     */
    @Override
    public void onStatusUpdate(String arg0, int arg1, String arg2) {
        // 通知导航sdk定位状态变化
        if (tencentNaviManger != null) {
            tencentNaviManger.onStatusUpdate(arg0, arg1, arg2);
        }
    }

    /**
     * 设置导航实例对象
     */
    public void setNavigationManager(TencentNavigation navimanager) {
        this.tencentNaviManger = navimanager;
        this.tencentNaviManger.setGetLatestLocationListener(tencentGetLatestLocation);
    }

    /**
     * 将定位sdk获取到的定位点转换成导航sdk需要的定位点类型
     */
    private GpsLocation convertToCommonLocation(TencentLocation location) {
        if (location == null) {
            return null;
        }
        GpsLocation gps = new GpsLocation();
        gps.accuracy = (int) location.getAccuracy();
        gps.altitude = location.getAltitude();
        gps.direction = location.getBearing();
        gps.latitude = location.getLatitude();
        gps.longitude = location.getLongitude();
        gps.provider = location.getProvider();
        gps.velocity = location.getSpeed();
        gps.time = location.getTime();

        return gps;
    }

}

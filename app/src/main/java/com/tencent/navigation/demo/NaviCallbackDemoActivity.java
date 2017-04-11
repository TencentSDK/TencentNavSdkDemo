package com.tencent.navigation.demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.iflytek.tts.TtsHelper;
import com.tencent.tencentmap.navisdk.TencentNavigation;
import com.tencent.tencentmap.navisdk.callback.navi.INavigationCallback;
import com.tencent.tencentmap.navisdk.callback.navi.ISearchRouteCallback;
import com.tencent.tencentmap.navisdk.callback.navi.ITtsListener;
import com.tencent.tencentmap.navisdk.data.GpsLocation;
import com.tencent.tencentmap.navisdk.data.NaviRoute;
import com.tencent.tencentmap.navisdk.data.NaviTypeEnum;
import com.tencent.tencentmap.navisdk.data.RouteSearchError;
import com.tencent.tencentmap.navisdk.data.TtsText;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;

/**
 * 导航sdk无页面算路播报导航demo
 *
 * @author selenali
 */
public class NaviCallbackDemoActivity extends FragmentActivity {

    /**
     * 算路按钮
     */
    private Button btnSearchRoute = null;

    /**
     * 状态输出view
     */
    private EditText editTexInfo = null;

    /**
     * 导航sdk实例
     */
    private TencentNavigation tencentNaviManager = null;

    /**
     * 定位点提供实例
     */
    private GpsNavi gpsInfo = null;

    /**
     * 底图对象
     */
    private TencentMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化demo页面和导航实例
        this.setContentView(R.layout.navicallback);

        SupportMapFragment fragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mMap = fragment.getMap();

        initTencentNavigation();

        editTexInfo = (EditText) findViewById(R.id.textshowinfo);

        btnSearchRoute = (Button) findViewById(R.id.btnsearchroute);
        btnSearchRoute.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击算路按钮，执行导航前的算路
                searchRoute();
            }
        });

    }

    /**
     * 算路方法
     */
    private void searchRoute() {
        // 假设一个起点
        GpsLocation startPosition = new GpsLocation();
        startPosition.latitude = 39.984130;
        startPosition.longitude = 116.307501;
        startPosition.direction = 180;

        // 假设一个终点
        LatLng destLatlng = new LatLng(39.982133, 116.314963);

        // 调用sdk的算路方法，需要设置起点、终点、算路的回调
        tencentNaviManager.calculateRoute(startPosition, destLatlng, searchRouteCallback);
    }

    /**
     * 初始化导航实例
     */
    private void initTencentNavigation() {
        // 初始化实例，并传入底图对象
        tencentNaviManager = new TencentNavigation(mMap.getMapView(), NaviTypeEnum.NAVI_TYPE_CAR);

        // 设置播报监听callback
        tencentNaviManager.setTtsListener(tts);

        // 设置是否开启路口放大图功能
        tencentNaviManager.setCrossingEnlargePictureEnable(true);

        // 设置是否开启电子眼图片功能
        tencentNaviManager.setElectriEyesPictureEnable(true);

        // 设置导航overlay是否显示
        tencentNaviManager.setNavigationOverlayEnable(false);

        // 设置导航中的callback
        tencentNaviManager.setNaviCallback(tencentNaviCallback);

        // 初始化定位点提供实例
        if (gpsInfo == null) {
            gpsInfo = new GpsNavi();
        }
        gpsInfo.enableGps(this);
        gpsInfo.setNavigationManager(tencentNaviManager);

    }

    /**
     * 导航中状态回调
     */
    private INavigationCallback tencentNaviCallback = new INavigationCallback() {

        @Override
        public void onOffRoute() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "已经偏航";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onArriveDestination() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "到达目的地";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onTurnDirection(int iDirection) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "转向图方向:" + iDirection;
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onSetDistanceToNextEvent(int dist) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "到下个转弯距离:" + dist;
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onSetDistanceTotalLeft(int dist) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "剩余总距离:" + dist;
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onSetTimeTotalLeft(int minutes) {

        }

        @Override
        public void onSetNextRoadName(String road) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "下个道路名称:" + road;
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onShowCameraEnlargement(String routeId, Drawable drawable) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "显示电子眼放大图";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onHideCameraEnlargement() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "隐藏电子眼放大图";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onShowCrossingEnlargement(String routeId, Drawable drawable) {
            Message msg = handUpdateUi.obtainMessage();
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onHideCrossingEnlargement() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "隐藏路口放大图";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onGpsStatusChanged(boolean arg0) {

        }

        @Override
        public void onGpsSwitched(boolean arg0) {

        }

        @Override
        public void onRecomputeRouteFinished(boolean arg0) {

        }

        @Override
        public void onRecomputeRouteStarted() {

        }

        @Override
        public void onSatelliteValidCountChanged(int arg0) {

        }

        @Override
        public void onTurnCompleted() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "转弯结束";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onTurnStart() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "开始转弯";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onEnterMountainRoad() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "进入山区路段";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onExitMountainRoad() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "离开山区路段";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onVoiceBroadcast(TtsText arg0) {

        }

        @Override
        public void onUpdateDrivingRoadName(String roadname) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "当前道路：" + roadname;
            handUpdateUi.sendMessage(msg);
        }
    };

    /**
     * 导航sdk算路回调
     */
    private ISearchRouteCallback searchRouteCallback = new ISearchRouteCallback() {
        @Override
        public void onBeginToSearch() {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "开始算路";
            handUpdateUi.sendMessage(msg);
        }

        @Override
        public void onFinishToSearch(ArrayList<NaviRoute> routs, RouteSearchError err) {
            Message msg = handUpdateUi.obtainMessage();
            msg.obj = "结束算路";
            handUpdateUi.sendMessage(msg);
            if (routs == null) {
                msg = handUpdateUi.obtainMessage();
                msg.obj = "算路失败，路线为空";
                handUpdateUi.sendMessage(msg);
                return;
            }
            msg = handUpdateUi.obtainMessage();
            msg.obj = "算路成功，开启模拟导航";
            handUpdateUi.sendMessage(msg);

            // 开启模拟导航功能
            tencentNaviManager.simulateNavi();
        }
    };

    /**
     * 导航sdk播报回调 FIXME:这里语音播报模块也不在sdk里，以下回调拿科大讯飞播报为例
     */
    private ITtsListener tts = new ITtsListener() {

        @Override
        public void initTts() {
            TtsHelper.getInstance().checkTTSFile(NaviCallbackDemoActivity.this);
        }

        @Override
        public void textToSpeech(TtsText ttsText) {
            com.iflytek.tts.TtsText readText = new com.iflytek.tts.TtsText();
            readText.text = ttsText.text;
            readText.assetPath = ttsText.path;
            TtsHelper.getInstance().read(readText, NaviCallbackDemoActivity.this);
        }
    };

    /**
     * editview 追加 文本
     */
    private void appShowInfo(String text) {
        if (editTexInfo == null) {
            return;
        }
        editTexInfo.append(text);
    }

    /**
     * 处理callback状态表述文本
     */
    private Handler handUpdateUi = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null || msg.obj == null) {
                return;
            }
            String strMsg = (String) msg.obj;
            strMsg += System.getProperty("line.separator");
            appShowInfo(strMsg);
        }

    };

    /**
     * 销毁导航实例
     */
    @Override
    protected void onDestroy() {
        tencentNaviManager.stopSimulateNavi();
        gpsInfo.disableGps();
        tencentNaviManager.onDestroy();

        super.onDestroy();
    }
}

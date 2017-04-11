# 腾讯导航SDK

## 简介
腾讯地图导航SDK（Android版）通过接口提供路线规划以及导航服务。基于Android 2.3及以上设备。腾讯地图导航流程简要概述如下：

腾讯地图导航SDK主要负责导航参数设置，如起终点、导航偏好、途径点等，然后进行路线规划。当路线规划成功后，将规划的导航路线传递给导航引擎，导航引擎结合定位信息，进行导航，然后再将导航信息回调给导航SDK。导航相关状态会通过导航SDK回调反馈给使用者，使用者可以根据需求做对应的处理。

TencentNavigation是腾讯导航SDK的接口层，需要导航功能的业务可以调用WeNavigation实现导航相关的功能。

## TencentNavigation接口层

TencentNavigation接口层并没有对应的实体类存在，而是一组对外的类和接口。
* Navigation：导航操作的封装类；
* GpsLocation：定位位置信息，用于导航中；
* NaviRoute：导航中使用的路线数据；
* WayPoint：导航途经点数据；
* NavConfig：导航的配置参数；
* ILocationChangedListener: 导航定位点回调
* INavigationCallback: 导航中状态回调
* ISearchRouteCallback: 查询路线回调
* ISearchOffRouteCallback: 查询偏航路线回调
* ITtsListener: 导航语音播报回调
* IOverSpeedListener: 导航中超速事件回调

## 集成方式

**使用aar方式引入，导航SDK需要依赖底图sdk**

``` java
compile 'com.tencent.map:tencent-map-vector-sdk:2.1.8.3'
```

## 使用方法

### 初始化

导航的操作都要通过Navigation对象控制,开发者首先需要实例化一个Navigation对象。

```java
public TencentNavigation(MapView map, NaviTypeEnum naviType);
```

实例化之后，可以用TencentNavigation对象进行导航的设置。


### 销毁
导航的销毁，当不使用导航时，需要对导航实例进行销毁，不建议设置setMapView为空。

```java
/**
* 导航销毁
*/
public void onDestroy()
```


### 导航回调

导航有三种回调，分别是导航事件回调，算路回调和偏航算路回调。
其中导航事件回调和偏航算路回调有专门的set方法进行设置。

```java
/**
* 设置导航指引回调
*/
public void setNaviCallback(INavigationCallback callback);
/**
* 设置偏航算路回调
*/
public void setSearchOffRouteCallback(ISearchOffRouteCallback callback)；
```

算路回调则需要在进行算路时传入。


### 算路

开始导航之前，需要一条导航路线，开发者可以通过算路方法获得。

```java
/**
* 算路
*/
public boolean calculateRoute(RouteSearchOptions searchOptions, ISearchRouteCallback callback)；
```
算路方法需要二个参数，算路参数和算路回调，算路之后，就可以发起导航。


### 算路并添加路线到地图

TencentNavigation提供一个方便的接口，直接规划路线并添加到地图上。

```java
/**
 *
 * 根据指定的起终点计算路线并添加到地图上展示
 *
 * @return 路线ID, 添加失败时返回-1.
 */
public int addRoute(final RouteSearchOptions searchOptions);

/**
 * 删除指定ID的路线
 *
 * @param routeId 路线ID,通过addRoute的返回值获得。
 */
public void removeRoute(int routeId);
```


### 算路后导航前

```java
/**
 *
 * 设置是否在规划路线后，直接把路线绘制到底图上（默认值为true）
 *
 * @param auto true：添加到底图 false：不添加到底图上，使用者可以从NaviRoute中获取点集自行处理
 */
public void setAutoChooseNaviRoute(boolean auto);
```

**导航时，如果之前使用了setAutoChooseNaviRoute这个方法，需要先设置导航使用的路线，然后再调用start方法开始导航。设置导航时的路线方法如下：**

```java
/**
 * 设置路线。可以使用其他方式计算好导航路线，然后使用该路线进行导航。
 *
 * @param route 已经获取的导航路线对象
 */
public void setNaviRoute(NaviRoute route);
```


### 开始及结束导航

规划路线成功，可以调用接口开始导航。
**导航和模拟导航的开始结束需要配对使用**
**开始导航需要开发者调用start方法，而结束导航除了调用stop方法主动结束之外，在导航到达目的地后，会自动结束导航。**

```java
// 开始导航
navigation.startNavi();
// 结束导航
navigation.stopNavi();
// 开始模拟导航
navigation.simulateNavi();
// 结束模拟导航
navigation.stopSimulateNavi();
```


### 导航中状态设置
提供接口可以设置导航过程中是否显示电子眼、路口放大图、以及起终点marker。
```java
/**
 * 是否开启路口放大图功能
 *
 * @param download true:开启  false：关闭
 */
public void setCrossingEnlargePictureEnable(boolean download);

/**
 * 是否开启电子眼放大图功能
 *
 * @param download true:开启  false：关闭
 */
public void setElectriEyesPictureEnable(boolean download);

/**
 * 是否绘制路线的起终点marker
 *
 * @param visible true:绘制  false：不绘制
 */
public void setMarkerOvelayVisible(boolean visible);
```

**注意下面接口，建议使用者不要轻易调用，设置true后，导航态会不显示路线。使用时需要了解清楚使用场景**

```java
/**
 * 是否显示导航的Overlay
 *
 * @param show true:显示  false：不显示
 */
public void setNavigationOverlayEnable(boolean show);
```

### 导航超速
提供设置回调方法，可以监听导航过程中的超速事件。导航中的超速分成两类：道路超速和电子眼超速。
```java
/**
 * 超速信息监听类
 *
 * @author selenali
 */
public void setOverSpeedListener(IOverSpeedListener listener);
```

### 导航定位
导航SDK不包含定位SDK，三方使用者可以自定义定位模块，建议使用腾讯定位SDK。由于导航SDK没有定位能力，所以需要三方使用者将定位点传递给导航SDK。

```java
/**
* 定位状态改变，在定位sdk的定位回调接口中调用
*
* @param provider
* @param status
* @param description
*/
public void onStatusUpdate(String provider, int status, String description);

/**
 * 定位位置改变，在定位sdk的定位回调接口中调用
 *
 * @param location 定位结果
 * @param error 错误状态码
 * @param reason 原因
 */
public void onLocationChanged(GpsLocation location, int error, String reason)
```

关于定位，还有一个相关的使用接口，使用者可以按需调用。

```java
/**
 * 设置回调，该回调用来获取最新的位置
 *
 * @param location
 */
public void setGetLatestLocationListener(ILocationChangedListener location)
```

### 导航播报
导航SDK不包含播报模块，设置语音播报的回调，SDK会将需要播报的文案、音效、优先级回调给使用者，使用者使用三方完成导航播报。

**建议的播报处理是：根据回调返回的TtsText结构中的优先级，后者优先级高，打断前者；后者优先级低，排队或丢弃。**

```java
/**
 * 设置tts模块
 *
 * @param listener
 */
public void setTtsListener(ITtsListener listener);
```

**三方使用者如果不使用科大讯飞播报SDK，还需要设置下面接口false。原因是科大讯飞的分隔符与其他语音播报模块不同**

```java
/**
 * 设置tts是否为科大讯飞
 *
 * @param iskedaxunfei true为科大讯飞（默认），false为其他
 */
public void setKeDaXunFei(boolean iskedaxunfei);
```

### 导航资源替换
三方使用者经常会有这样的需要，自定义导航中的资源和样式。是否使用导航SDK中的默认资源，可以通过下面方法设置。

```java
/**
* 设置是否使用默认图标资源
*
* @param bodefault true：使用默认资源（默认）  false：不使用默认资源
*/
public void setUseDefaultRes(boolean bodefault);
```

使用时设置不使用默认资源，三方使用者需要将自定义资源放入应用的assets目录下，资源命名要求保持和导航SDK中assets下的资源命名相同。这样在运行导航资源会使用三方使用者的自定义资源。

导航态默认的导航页面也可以关闭，方法如下：

```java
/**
 * 设置默认的导航界面是否显示
 *
 * @param visible
 */
public void setNavOverlayVisible(boolean visible);
```

### 路线效果设置
导航SDK可以动态设置路线的宽度，路线的边距。

```java
/**
 * 设置导航路线的宽度
 *
 * @param width 必须大于0
 */
public void setNavigationLineWidth(int width);

/**
 * 设置导航路线的边距
 *
 * @param left
 * @param right
 * @param top
 * @param bom
 */
public void setNavigationLineMargin(int left, int right, int top, int bom);
```

### 视野效果
介绍两个和视野相关的方法。使用者如果没有特殊需求不用设置，SDK会默认使用。效果体验可以安装demo体验。

```java
/**
 * 设置是否能够进行路线全览的动画
 *
 * @param isEnable
 */
public void setZoomToRouteAnimEnable(boolean isEnable);
```


### 日志记录
为了方便接入，提供日志开关。

```java
/**
 * 设置导航日志器
 *
 * @param logger
 */
public void setNavLogger(NavLogger logger);
```


### 路线及导航信息获取
三方使用者还可以获取一些信息，例如：剩余途径点、当前路线对象、剩余时间、路线bound等。


[NavigationDemo](../app/src/main/java/com/tencent/navigation/demo/TencentNaviMapActivity.java)


## 版权声明

TencentNavigation腾讯内部组件，版权和解释权归腾讯所有。

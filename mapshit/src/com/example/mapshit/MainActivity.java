package com.example.mapshit;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	// 定位SDK的核心类
	private LocationClient mLocClient;
	//定位图层显示方式
	private LocationMode mCurrentMode;
	//bitmap 描述信息
	BitmapDescriptor mCurrentMarker;
	//MapStatusUpdate可以设置缩放级别等，newLatLng设置地图新中心点
	MapStatusUpdate msu;
	// 是否首次定位
	private boolean isFirstLoc = true;
	//一个显示地图的视图（View）。它负责从服务端获取地图数据。它将会捕捉屏幕触控手势事件。 
	MapView mMapView;
	BaiduMap mBaiduMap;
	//Interface definition for a callback to be invoked when the checked radio button changed in this group.
	private Button requestLocButton;
	//定位SDK监听函数
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isRequest=false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       SDKInitializer.initialize(getApplicationContext());
	       setContentView(R.layout.activity_main);
	       requestLocButton = (Button) findViewById(R.id.request);
			//Interface definition for a callback to be invoked when a view is clicked.
		   OnClickListener btnClickListener = new OnClickListener() {
				//Called when a view has been clicked.
				public void onClick(View v) {
					requestLocation();
				}
			};
			//Register a callback to be invoked when this view is clicked. If this view is not clickable, it becomes clickable.
			requestLocButton.setOnClickListener(btnClickListener);
			// 地图初始化
			mMapView = (MapView)findViewById(R.id.bmapView);
			mBaiduMap = mMapView.getMap();
			//设置缩放级别
			msu = MapStatusUpdateFactory.zoomTo(17.0f);
			mBaiduMap.setMapStatus(msu);
			// 开启定位图层
			mBaiduMap.setMyLocationEnabled(true);
			// 定位初始化
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			mLocClient.setLocOption(option);
			mLocClient.start();
    }
    
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			//MyLocationData.Builder定位数据建造器
			MyLocationData locData = new MyLocationData.Builder()
			        .accuracy(location.getRadius())
	                .latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			//位置发生变化
			if (isFirstLoc || isRequest) {
				//地理坐标基本数据结构
				LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
				msu = MapStatusUpdateFactory.newLatLng(loc);
				mBaiduMap.animateMapStatus(msu);
				//添加图层进行画线，如果没有初始……
				if(true){
					
				}
				else{
					
				}
				isRequest = false;
			}
			isFirstLoc = false;
		}
		
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/**
	 * 手动请求定位的方法
	 */
	public void requestLocation() {
		isRequest = true;

		if (mLocClient != null && mLocClient.isStarted()) {
			// 调用下面的方法,显示出正在定位
			Toast.makeText(MainActivity.this, "正在定位...",Toast.LENGTH_SHORT).show();
			// 定位结果在ReceiveListener的方法OnReceive方法的参数中返回。
			mLocClient.requestLocation();
		} else {
			// d代表debug在控制台输出错误
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}
	
	/**
	 * 设置定位参数
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPS
//		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll"); // 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
//		option.setIsNeedAddress(true); // 返回的定位结果包含地址信息
//		option.setNeedDeviceDirect(true); // 返回的定位结果包含手机机头的方向
		mLocClient.setLocOption(option);
	}
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}

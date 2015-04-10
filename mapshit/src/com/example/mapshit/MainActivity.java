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

	// ��λSDK�ĺ�����
	private LocationClient mLocClient;
	//��λͼ����ʾ��ʽ
	private LocationMode mCurrentMode;
	//bitmap ������Ϣ
	BitmapDescriptor mCurrentMarker;
	//MapStatusUpdate�����������ż���ȣ�newLatLng���õ�ͼ�����ĵ�
	MapStatusUpdate msu;
	// �Ƿ��״ζ�λ
	private boolean isFirstLoc = true;
	//һ����ʾ��ͼ����ͼ��View����������ӷ���˻�ȡ��ͼ���ݡ������Ჶ׽��Ļ���������¼��� 
	MapView mMapView;
	BaiduMap mBaiduMap;
	//Interface definition for a callback to be invoked when the checked radio button changed in this group.
	private Button requestLocButton;
	//��λSDK��������
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
			// ��ͼ��ʼ��
			mMapView = (MapView)findViewById(R.id.bmapView);
			mBaiduMap = mMapView.getMap();
			//�������ż���
			msu = MapStatusUpdateFactory.zoomTo(17.0f);
			mBaiduMap.setMapStatus(msu);
			// ������λͼ��
			mBaiduMap.setMyLocationEnabled(true);
			// ��λ��ʼ��
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// ��gps
			option.setCoorType("bd09ll"); // ������������
			option.setScanSpan(1000);
			mLocClient.setLocOption(option);
			mLocClient.start();
    }
    
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ���ٺ��ڴ����½��յ�λ��
			if (location == null || mMapView == null)
				return;
			//MyLocationData.Builder��λ���ݽ�����
			MyLocationData locData = new MyLocationData.Builder()
			        .accuracy(location.getRadius())
	                .latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			//λ�÷����仯
			if (isFirstLoc || isRequest) {
				//��������������ݽṹ
				LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
				msu = MapStatusUpdateFactory.newLatLng(loc);
				mBaiduMap.animateMapStatus(msu);
				//���ͼ����л��ߣ����û�г�ʼ����
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
	 * �ֶ�����λ�ķ���
	 */
	public void requestLocation() {
		isRequest = true;

		if (mLocClient != null && mLocClient.isStarted()) {
			// ��������ķ���,��ʾ�����ڶ�λ
			Toast.makeText(MainActivity.this, "���ڶ�λ...",Toast.LENGTH_SHORT).show();
			// ��λ�����ReceiveListener�ķ���OnReceive�����Ĳ����з��ء�
			mLocClient.requestLocation();
		} else {
			// d����debug�ڿ���̨�������
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}
	
	/**
	 * ���ö�λ����
	 */
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // ��GPS
//		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType("bd09ll"); // ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5000); // ���÷���λ����ļ��ʱ��Ϊ5000ms
//		option.setIsNeedAddress(true); // ���صĶ�λ���������ַ��Ϣ
//		option.setNeedDeviceDirect(true); // ���صĶ�λ��������ֻ���ͷ�ķ���
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
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}

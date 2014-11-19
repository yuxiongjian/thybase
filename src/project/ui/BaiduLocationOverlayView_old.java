package project.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.thybase.R;

import project.ui.BMapUtil;
import project.ui.ProjectActivity;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 
 */
public class BaiduLocationOverlayView extends RelativeLayout //ViewGroup
{
	/**
	 * 
	 */
	private static final long serialVersionUID = BaiduLocationOverlayView.class
			.hashCode();

	protected final int LOC_TIMER = 0;
	// public BDLocation last_location=null;
	protected ViewGroup container;
	ProjectActivity ThisAct;
	protected LayoutInflater mInflater;
	protected int time_span = (int) (2 * 6);// seconds;
	// public LocRecordController mLocContrl = null;

	public BaiduLocationOverlayView(ProjectActivity thisAct2,
			LayoutInflater inflater, ViewGroup container) {
		super(thisAct2);
		// mLocContrl = new LocRecordController(this, thisAct2);
		this.ctx = thisAct2;
		ThisAct = thisAct2;
		this.container = container;
		this.mInflater = inflater;

		// TODO Auto-generated constructor stub
	}

	public BaiduLocationOverlayView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		
		this.ctx = context;
		createView();

		// container = context.
		// TODO Auto-generated constructor stub
	}

	public BaiduLocationOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx =  context;

		
		createView(context, null);
		// TODO Auto-generated constructor stub
	}

	public BaiduLocationOverlayView(Context context) {
		super(context);
		this.ctx = context;
		createView();
		// TODO Auto-generated constructor stub
	}

	private enum E_BUTTON_TYPE {
		LOC, COMPASS, FOLLOW
	}

	private E_BUTTON_TYPE mCurBtnType;

	// 定位相关
	LocationClient mLocClient;
	MyLocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();

	private TextView popupText = null;// 泡泡view
	private View viewCache = null;

	// 地图相关，使用继承MapView的MyLocationMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null; // 地图View
	protected BaiduMap mBaiduMap = null;

	// UI相关
	OnCheckedChangeListener radioButtonListener = null;
	// Button requestLocButton = null;

	// boolean isRequest = false;// 是否手动触发请求定位
	// boolean isFirstLoc = true;// 是否首次定位

	protected Context ctx;

	protected Date lastTime = null;

	private static View getRootView(Activity act) {
		return ((ViewGroup) act.findViewById(android.R.id.content))
				.getChildAt(0);
	}

	public View createView() {
		return createView(ctx, mInflater);
	}

	Handler handler = null;

	int layout = 0;
	public View createView(Context context, LayoutInflater inflater){

		
		layout = R.layout.edit_scan;
	
	
		rootV = LayoutInflater.from(context).inflate(R.layout.thy_baidu_loc_view, this, true);   
		
		return rootV;
	}
	
	
	private ToggleButton followBtn;

	public View createView(Activity ctx, LayoutInflater inflater,
			ViewGroup container) {

		layout = R.layout.thy_baidu_loc_view;
		layout = R.layout.edit_scan;

		if (inflater == null)
			inflater = LayoutInflater.from(ctx);
		rootV = View.inflate(ctx, layout, null);
		//this.inflate( (layout, this,true);
		//rootV = inflater.inflate(layout, this,true);
		CharSequence titleLable = "定位功能";
		addView(rootV);
		android.widget.CompoundButton.OnCheckedChangeListener listener = new android.widget.CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				//followBtn.setChecked(isChecked);
				if (isChecked)
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									LocationMode.FOLLOWING, true, null));
				// myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				else
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									LocationMode.NORMAL, true, null));

				// myLocationOverlay.setLocationMode(LocationMode.NORMAL);
			}

		};

		// 地图初始化
		mMapView = (MapView) rootV.findViewById(R.id.bmapView);
		mMapView.setBackgroundColor(Color.WHITE);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		// MapStatusUpdateFactory.
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapStatus(msu);
		mBaiduMap.setMyLocationEnabled(true);

		mLocClient = new LocationClient(ctx);

		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.disableCache(true);
		mLocClient.setLocOption(option);
		mLocClient.start();

		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.FOLLOWING, true, null));

		requestLoc();
		Toast.makeText(ctx, "正在定位……", Toast.LENGTH_SHORT).show();
		// 修改定位数据后刷新图层生效
		// mBaiduMap..refresh();
		// requestLocButton.performClick();
		return rootV;
	}
	/*
	 @Override
     protected void onLayout(boolean changed, int l, int t, int r, int b) {
             // TODO Auto-generated method stub
             Log.d("onlayout","layout");
          //   rootV.layout(l,0,800,600);
     }
     protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
             int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
                             int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
   
                            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
                            int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
                            Log.d("widthSpecSize",String.valueOf(widthSpecSize));
                            setMeasuredDimension(widthSpecSize, heightSpecSize);
     }*/

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLoc() {
		// isRequest = true;
		mLocClient.requestLocation();

	}

	/**
	 * 修改位置图标
	 * 
	 * @param marker
	 */
	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		// myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效

	}

	/**
	 * 显示location所在位置的地图
	 */

	public void moveMapToLoc(BDLocation location) {

		// 移动地图到定位点
		Log.d("LocationOverlay", "receive location, animate to it");

		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);

		// myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
		// requestLocButton.setText("跟随");
		// mCurBtnType = E_BUTTON_TYPE.FOLLOW;

	}

	public class MyLocationListenner implements BDLocationListener {

		public MyLocationListenner() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceiveLocation(BDLocation location) {

			onUpdateLocation(location);
			if (location == null)
				return;

		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public void onUpdateLocation(BDLocation location) {

		MyLocationData locData = new MyLocationData.Builder()
				.accuracy(location.getRadius())
				// 此处设置开发者获取到的方向信息，顺时针0-360
				.direction(100).latitude(location.getLatitude())
				.longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);

	}

	public void clearMap() {

		mBaiduMap.clear();

	}

	public void drawMap(LatLng... pts) {
		clearMap();
		BDLocation bd = null;

		for (LatLng gp : pts) {

			if (gp != null) {
				if (bd == null) {
					bd = new BDLocation();
					bd.setLatitude(gp.latitude);
					bd.setLongitude(gp.longitude);
				}
				addPointToMap(gp.latitude, gp.longitude);
			}
		}
		this.moveMapToLoc(bd);
	}

	public void addPointToMap(double mLat, double mLon) {

		LatLng cur_pt = new LatLng(mLat, mLon);

		// 添加折线
		if (lastDrawPoint != null) {

			List<LatLng> points = new ArrayList<LatLng>();
			points.add(lastDrawPoint);
			points.add(cur_pt);
			OverlayOptions ooPolyline = new PolylineOptions().width(6)
					.color(0xFF888888).points(points);
			mBaiduMap.addOverlay(ooPolyline);
			OverlayOptions ooDot = new DotOptions().center(lastDrawPoint)
					.radius(12).color(0xFF0000FF);
			mBaiduMap.addOverlay(ooDot);

		}
		OverlayOptions ooDot = new DotOptions().center(cur_pt).radius(12)
				.color(0xFF0000FF);
		mBaiduMap.addOverlay(ooDot);
		lastDrawPoint = cur_pt;

	}

	private LatLng lastDrawPoint = null;

	protected View rootV;

	

}

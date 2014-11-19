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
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.thybase.R;

import project.ui.BMapUtil;
import project.ui.ProjectActivity;
import project.util.ProjectLocationListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
public class BDLocationLayout extends //ViewGroup 
RelativeLayout 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = BDLocationLayout.class
			.hashCode();

	protected final int LOC_TIMER = 0;
	// public BDLocation last_location=null;
	protected ViewGroup container;
	ProjectActivity ThisAct;
	protected LayoutInflater mInflater;
	protected int time_span = (int) (2 * 6);// seconds;
	// public LocRecordController mLocContrl = null;

	public BDLocationLayout(ProjectActivity thisAct2,Context context,
			LayoutInflater inflater, ViewGroup container) {
		super(thisAct2);
		// mLocContrl = new LocRecordController(this, thisAct2);
		this.ctx = thisAct2;
		ThisAct = thisAct2;
		this.container = container;
		this.mInflater = inflater;

		// TODO Auto-generated constructor stub
	}

	public BDLocationLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		if( ProjectActivity.class.isInstance(context) )
			ThisAct = (ProjectActivity) context;
		this.ctx = context;
		createView();

		// container = context.
		// TODO Auto-generated constructor stub
	}

	public BDLocationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx =  context;
		if( ProjectActivity.class.isInstance(context) )
			ThisAct = (ProjectActivity) context;
		
		createView();
		// TODO Auto-generated constructor stub
	}

	public BDLocationLayout(Context context) {
		super(context);
		if( ProjectActivity.class.isInstance(context) )
			ThisAct = (ProjectActivity) context;
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
	/*
	public View createView(Context context, LayoutInflater inflater){

		
		layout = R.layout.thy_baidu_loc_view;
	
	
		//rootV = LayoutInflater.from(context).inflate(R.layout.thy_baidu_loc_view, this, true);   
		
		rootV = View.inflate(context, layout, this);  
		
		return rootV;
	}
	
	*/
	private ToggleButton followBtn;
    public double getArea(LatLng pts[] ){
    	
    	//function calcArea(PointX,PointY,MapUnits) {
    	 double mtotalArea = 0;
    	    int Count = pts.length;
    	    if (Count>3) {//至少3个点
    	       
    	       
    	        
    	       
    	            double LowX=0.0;
    	            double LowY=0.0;
    	            double MiddleX=0.0;
    	            double MiddleY=0.0;            
    	            double HighX=0.0;
    	            double HighY=0.0;

    	            double AM = 0.0;        
    	            double BM = 0.0;    
    	            double CM = 0.0;            

    	            double AL = 0.0;        
    	            double BL = 0.0;    
    	            double CL = 0.0;        
    	    
    	            double AH = 0.0;        
    	            double BH = 0.0;    
    	            double CH = 0.0;            

    	            double CoefficientL = 0.0;//Coefficient系数
    	            double CoefficientH = 0.0;        
    	                        
    	            double ALtangent = 0.0; //tangent切线       
    	            double BLtangent = 0.0;    
    	            double CLtangent = 0.0;    

    	            double AHtangent = 0.0;        
    	            double BHtangent = 0.0;    
    	            double CHtangent = 0.0;
    	                                    
    	            double ANormalLine = 0.0; //NormalLine法线       
    	            double BNormalLine = 0.0;    
    	            double CNormalLine = 0.0;
    	                                                
    	          double OrientationValue = 0.0; //Orientation  Value方向 值  
    	          
    	          double AngleCos = 0.0;//余弦角

    	          double Sum1 = 0.0; 
    	          double Sum2 = 0.0; 
    	          int Count2 = 0;           
    	          int Count1 = 0; 
    	      
    	          
    	          double Sum = 0.0;
    	          double Radius = 6378000; //半径
    	      
    	            for(int i=0;i<Count;i++)
    	            {
    	                if(i==0)
    	                {
    	                    LowX = pts[Count-1].latitude * Math.PI / 180;//换算成弧度
    	                    LowY = pts[Count-1].longitude * Math.PI / 180;    
    	                    MiddleX = pts[0].latitude * Math.PI / 180;
    	                    MiddleY = pts[0].longitude * Math.PI / 180;
    	                    HighX = pts[1].latitude * Math.PI / 180;
    	                    HighY = pts[1].longitude * Math.PI / 180;
    	                }
    	                else if(i==Count-1)
    	                {
    	                    LowX = pts[Count-2].latitude * Math.PI / 180;
    	                    LowY = pts[Count-2].longitude * Math.PI / 180;    
    	                    MiddleX = pts[Count-1].latitude * Math.PI / 180;
    	                    MiddleY = pts[Count-1].longitude * Math.PI / 180;            
    	                    HighX = pts[0].latitude * Math.PI / 180;
    	                    HighY = pts[0].longitude * Math.PI / 180;                        
    	                }
    	                else
    	                {
    	                    LowX = pts[i-1].latitude * Math.PI / 180;
    	                    LowY = pts[i-1].longitude * Math.PI / 180;    
    	                    MiddleX = pts[i].latitude * Math.PI / 180;
    	                    MiddleY = pts[i].longitude * Math.PI / 180;            
    	                    HighX = pts[i+1].latitude * Math.PI / 180;
    	                    HighY = pts[i+1].longitude * Math.PI / 180;                            
    	                }
    	    
    	                AM = Math.cos(MiddleY) * Math.cos(MiddleX);
    	                BM = Math.cos(MiddleY) * Math.sin(MiddleX);
    	                CM = Math.sin(MiddleY);
    	                AL = Math.cos(LowY) * Math.cos(LowX);
    	                BL = Math.cos(LowY) * Math.sin(LowX);
    	                CL = Math.sin(LowY);
    	                AH = Math.cos(HighY) * Math.cos(HighX);
    	                BH = Math.cos(HighY) * Math.sin(HighX);
    	                CH = Math.sin(HighY);        
    	                                
    	                    
    	                CoefficientL = (AM*AM + BM*BM + CM*CM)/(AM*AL + BM*BL + CM*CL);
    	                CoefficientH = (AM*AM + BM*BM + CM*CM)/(AM*AH + BM*BH + CM*CH);
    	                
    	                ALtangent = CoefficientL * AL - AM;
    	                BLtangent = CoefficientL * BL - BM;
    	                CLtangent = CoefficientL * CL - CM;
    	                AHtangent = CoefficientH * AH - AM;
    	                BHtangent = CoefficientH * BH - BM;
    	                CHtangent = CoefficientH * CH - CM;                
    	                
    	                
    	                AngleCos = (AHtangent * ALtangent + BHtangent * BLtangent + CHtangent * CLtangent)/(Math.sqrt(AHtangent * AHtangent + BHtangent * BHtangent +CHtangent * CHtangent) * Math.sqrt(ALtangent * ALtangent + BLtangent * BLtangent +CLtangent * CLtangent));
    	                
    	                AngleCos = Math.acos(AngleCos);
    	                
    	                ANormalLine = BHtangent * CLtangent - CHtangent * BLtangent;
    	                BNormalLine = 0 - (AHtangent * CLtangent - CHtangent * ALtangent); 
    	                CNormalLine = AHtangent * BLtangent - BHtangent * ALtangent;
    	                
    	                if(AM!=0)            
    	                    OrientationValue = ANormalLine/AM;
    	                else if(BM!=0)                    
    	                    OrientationValue = BNormalLine/BM;
    	                else
    	                    OrientationValue = CNormalLine/CM;
    	                        
    	                if(OrientationValue>0)
    	                {
    	                        Sum1 += AngleCos;
    	                        Count1 ++;
    	                        
    	                }
    	                else
    	                {
    	                        Sum2 += AngleCos;
    	                        Count2 ++;
    	                        //Sum +=2*Math.PI-AngleCos;
    	                }

    	            }
    	                
    	            if(Sum1>Sum2){
    	                Sum = Sum1+(2*Math.PI*Count2-Sum2);
    	            }
    	            else{
    	                Sum = (2*Math.PI*Count1-Sum1)+Sum2;
    	            }
    	            
    	            //平方米
    	            mtotalArea = (Sum-(Count-2)*Math.PI)*Radius*Radius;
    	        }
    	       
    
			return mtotalArea;
    	
    	
    }
	public View createView(Context ctx, LayoutInflater inflater) {

		layout = R.layout.thy_baidu_loc_view;
		
		if (inflater == null)
			inflater = LayoutInflater.from(ctx);
		rootV = inflater.inflate( layout, this,true);
		//this.inflate( (layout, this,true);
		//rootV = inflater.inflate(layout, this,true);
		CharSequence titleLable = "定位功能";
		//addView(rootV);
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
		
		Message bdm = Message.obtain( ThisAct.msgHandler, ProjectLocationListener.class.hashCode());
		bdm.obj = location;
		bdm.sendToTarget();
		

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

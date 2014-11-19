package project.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.thybase.R;

/**
 * 演示MapView的基本用法
 */
public class BaseBaiduMap extends ProjectActivity {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = BaseBaiduMap.class.hashCode();

	public BaseBaiduMap() {
		super();
		
		// TODO Auto-generated constructor stub
	}


	final static String TAG = "MainActivity";
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	protected MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	GeoPoint p ;
    double cLat = 39.945 ;
    double cLon = 116.404 ;
	
    /**
	 * @return the cLat
	 */
	public double getcLat() {
		return cLat;
	}

	/**
	 * @param cLat the cLat to set
	 */
	public void setcLat(double cLat) {
		this.cLat = cLat;
	}

	/**
	 * @return the cLon
	 */
	public double getcLon() {
		return cLon;
	}

	/**
	 * @param cLon the cLon to set
	 */
	public void setcLon(double cLon) {
		this.cLon = cLon;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
       
        /*if (mBMapManager == null) {
            mBMapManager = new BMapManager(this);
            *//**
             * 如果BMapManager没有初始化则初始化BMapManager
             *//*
            mBMapManager.init(strKey,new MyGeneralListener());
        }*/
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
      //  setContentView(R.layout.activity_main);
        mMapView = (MapView)findViewById(R.id.bmapView);
        int w = mMapView.getWidth();
       // mMapView.setLayoutParams(new TableRow.LayoutParams(Handphone.handphone.outSize.right,LayoutParams.MATCH_PARENT));
         w= w+1;
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(12);
       
        /**
         * 将地图移动至指定点
         * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index.html查询地理坐标
         * 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
         */
        
        Intent  intent = getIntent();
        if ( intent.hasExtra("x") && intent.hasExtra("y") ){
        	//当用intent参数时，设置中心点为指定点
        	Bundle b = intent.getExtras();
        	p = new GeoPoint(b.getInt("y"), b.getInt("x"));
        }else{
        	//设置中心点为天安门
        	 p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
        }
        
        mMapController.setCenter(p);
        
        /**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				String title = "";
				if (mapPoiInfo != null){
					title = mapPoiInfo.strText;
					Toast.makeText(BaseBaiduMap.this,title,Toast.LENGTH_SHORT).show();
					mMapController.animateTo(mapPoiInfo.geoPt);
				}
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
			}
            /**
             * 在此处理地图载完成事件 
             */
			@Override
			public void onMapLoadFinish() {
				Toast.makeText(BaseBaiduMap.this, 
						       "地图加载完成", 
						       Toast.LENGTH_SHORT).show();
				
			}
		};
		mMapView.regMapViewListener(mBMapManager, mMapListener);
    }
    
    @Override
    protected void onPause() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
    	 */
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
    	 */
        mMapView.destroy();
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
 
    public static boolean m_bKeyRight = true;
    static BMapManager mBMapManager = null;

    public static final String strKey = "9uCLrM1eY1oWMIsSKlAXvkZt";
	
	
	
	static public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener(context))) {
            Toast.makeText(context, 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	

	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
   static  class MyGeneralListener implements MKGeneralListener {
        
        private Context context;

		public MyGeneralListener(Context context) {
        	this.context = context;
			// TODO Auto-generated constructor stub
		}

		@Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(context, "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(context, "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(context, 
                        "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                m_bKeyRight = false;
            }
            else{
            	m_bKeyRight = true;
            	Toast.makeText(context, 
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
    
}

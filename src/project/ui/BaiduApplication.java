package project.ui;


import hardware.config.Handphone;
import project.util.GlobalExceptionHandler;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;


public class BaiduApplication extends Application {
	
    private static BaiduApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

    public static  String strKey = "";// "9uCLrM1eY1oWMIsSKlAXvkZt";
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(
				this));
		initEngineManager(this);
	}
	
	public void initEngineManager(Context context) {
        try {
			if (mBMapManager == null && !Handphone.isVMIntel()) {
			    mBMapManager = new BMapManager(context);
			}

			if (mBMapManager== null || !mBMapManager.init(strKey,new MyGeneralListener())) {
			    Toast.makeText(BaiduApplication.getInstance().getApplicationContext(), 
			            "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BaiduApplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(BaiduApplication.getInstance().getApplicationContext(), "无法启动地图",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(BaiduApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(BaiduApplication.getInstance().getApplicationContext(), 
                        "请检查strKey输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                BaiduApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	BaiduApplication.getInstance().m_bKeyRight = true;
            	//Toast.makeText(BaiduApplication.getInstance().getApplicationContext(), "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
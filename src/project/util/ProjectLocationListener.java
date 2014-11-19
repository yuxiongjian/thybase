package project.util;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ProjectLocationListener implements LocationListener {
	static ArrayList<Handler> hds = new ArrayList<Handler>();

	static public void regHandle(Handler h) {

		if (h== null && hds.contains(h))
			return;
		else
			hds.add(h);

	}

	static public void unRegHandle(Handler h) {

		if (hds.contains(h))
			hds.remove(h);

	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}

	public static Location getCurLoc() {
		if (ProjectLocationListener.locationManager == null ) {
			curLoc = openLocationService(ctx);

		}
		
		if (ProjectLocationListener.locationManager== null || !ProjectLocationListener.locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
		
			Location lo = new Location("GPS CLOSED");
			lo.setLongitude(-1.0);
			lo.setLatitude(-1.0);
			return lo;
		}
		if (curLoc == null){
			return new Location("NO GPS SIG");
		}
		return curLoc;
	}

	public static void setCurLoc(Location curLoc) {
		ProjectLocationListener.curLoc = curLoc;
	}

	public static String getProvider() {
		return provider;
	}

	public static void setProvider(String provider) {
		ProjectLocationListener.provider = provider;
	}

	public static LocationManager getLocationManager() {
		return locationManager;
	}

	public static void setLocationManager(LocationManager locationManager) {
		ProjectLocationListener.locationManager = locationManager;
	}

	public static Context ctx;
	static private Location curLoc = null;
	static public String provider = null;
	static public LocationManager locationManager;

	public ProjectLocationListener(LocationManager locationManager) {

		ProjectLocationListener.locationManager = locationManager;

		// TODO Auto-generated constructor stub
	}

	@Override
	public void onLocationChanged(Location location) {

		curLoc = location;
		


		MyLog.Log("GPS坐标："+curLoc);

		for (Handler hd : hds) {
			Message msg = hd.obtainMessage();
			msg.what=ProjectLocationListener.class.hashCode();
			msg.obj=location;
			//Bundle data = new Bundle(); // data.putSerializable(key, value);
			//data.putSerializable("location", (Serializable) location);
			//msg.setData(data);
			hd.sendMessage(msg);
		}

		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		MyLog.Log(provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		MyLog.Log(provider);
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		MyLog.Log(provider);

	}

	static boolean openGPSSettings(Context ctx) {
		if( ctx == null)
			return false;
		ProjectLocationListener.ctx = ctx;
		LocationManager alm = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// Toast.makeText(ctx, "GPS模块正常", Toast.LENGTH_SHORT).show();
			return true;
		}

		Toast.makeText(ctx, "请开启GPS！", Toast.LENGTH_SHORT).show();
		return false;

	}

	public static Location openLocationService(Context ctx) {
		// 获取位置管理服务
		if (openGPSSettings(ctx) == false)
			;// return null;
	
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) ctx.getSystemService(serviceName);
		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 精度
		//criteria.setAccuracy(Criteria.ACCURACY_LOW); // 精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT); // 低功耗

		provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
		if ( provider==null){
			Toast.makeText(ctx, "目前无法定位您的手机", Toast.LENGTH_LONG).show();
			return null;
		}
		
		curLoc = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
		// updateToNewLocation(location);
		// 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
		locationManager.requestLocationUpdates(provider, 0 * 1000, (float) 0,
				new ProjectLocationListener(locationManager));
		return null;
	}

}

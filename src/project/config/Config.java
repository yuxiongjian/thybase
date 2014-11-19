package project.config;

import project.config.DebugSetting.DEBUG;
import project.util.sqldb.dao.Student;
import project.util.sqldb.dao.Teacher;
import android.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;

public class Config {
	//static { DebugSetting.debug = DEBUG.REALDEBUG;}public static  String 真实模拟id = "99000458439577";//"863034021964403";// "99000458447592";//Config.devicid;// "99000458447611";
	static { DebugSetting.debug = DEBUG.RELEASE;}public static  String 真实模拟id = null;
	public static  String SHAREDPREFERENCES = "chicmobapp";	
	private static  String TAG = "Config";
	public static  String mainUrl = "http://app.jitoa.com.cn";
	public static  String testUrl = "http://appdev.jitoa.com.cn:8888";
	public static  String UPDATE_SERVER = testUrl+"/client/"; 
	public static  String UPDATE_APKNAME = "jiatu.apk";
	public static  String UPDATE_VERJSON = "jiatu.json";
	public static  String UPDATE_SAVENAME = "jiatu.apk";
	
	public static  Class<?>[] DBCLASS ={}// { MobileLocation.class, Teacher.class, Student.class ,WorkOrder.class,WorkRequest.class, RecordCopy.class, PODetail.class};
	;
	public static  String DBNAME = "com_jt_jiatuapp";
	public static int 			DBVERSION = 50;
	public static  String devicid = "863034021964403";
	public static  String Password = "123456";
	public static  double lot=121.520185689796 ;
	public static  double lat=31.1904585576853;
	public static  String BARCODE = "MB2014040100018708";
	public static  int PONUM =10;
	
	public static String mobileType="";

	
	public static String mainurls[]= new String[]{"",mainUrl+"/api/wo/workRequest!findWorkOrder.action"
	,mainUrl+"/api/wo/workRequest!findDelivery.action"
	,mainUrl+"/api/wo/repairHelp!find.action"
	,mainUrl+"/api/wo/repairHelp!find.action"
	,mainUrl+"/api/wo/workRequest!findTomorrowWorkOrder.action"};
	
	public static String testurls[]= new String[]{"",testUrl+"/api/wo/workRequest!findWorkOrder.action"
		,testUrl+"/api/wo/workRequest!findDelivery.action"
		,testUrl+"/api/wo/repairHelp!find.action"
		,testUrl+"/api/wo/repairHelp!find.action"
		,testUrl+"/api/wo/workRequest!findTomorrowWorkOrder.action"};
	public static String ServiceUrl = "";
	public static String _diServiceUrl = "";
	public static String _doServiceUrl = "";
	public static String[] urls;

	public static void init() {
		ServiceUrl = mainUrl+"/api/jsonrpc/"+ mobileType;
		 _diServiceUrl = "";
		_doServiceUrl = testUrl+"/api/jsonrpc/"+ mobileType;
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the url
	 */
	public static String[] getUrls() {
		if(DebugSetting.isRelease())
			return mainurls;
		else if(DebugSetting.debug==DebugSetting.DEBUG.REALDEBUG)
			return mainurls;
		else
			return testurls;
	}

	/**
	 * @param url the url to set
	 */
	public static void setUrls(String[] url) {
		Config.urls = url;
	}

	/*
	 * case COMPLEX_UNIT_PX: return value; case COMPLEX_UNIT_SP: return value *
	 * metrics.scaledDensity; case COMPLEX_UNIT_PT: return value * metrics.xdpi
	 * * (1.0f/72);
	 */
	public static DisplayMetrics getMetrics(Context ct) {

		DisplayMetrics metrics = null;
		metrics = ct.getResources().getDisplayMetrics();
		return metrics;
	}

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {

			String name = context.getPackageName();
			verCode = context.getPackageManager().getPackageInfo(name, 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			String name = context.getPackageName();
			verName = context.getPackageManager().getPackageInfo(name, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;

	}

	public static String getAppName(Context context, int id) {
		String verName = context.getResources().getText(id)
				.toString();
		return verName;
	}

	
}

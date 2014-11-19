/**
 * 
 */
package hardware.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import project.config.Config;
import project.config.DebugSetting;
import project.config.DebugSetting.DEBUG;
import project.pojo.Bpojo;
import project.pojo.Hphone;
import project.util.MyLog;

import com.google.gson.Gson;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author thomasy
 * 
 */
public class Handphone extends Bpojo{
	/**
	 * 
	 */
	private static final long serialVersionUID = Handphone.class.hashCode();

	/**
	 * 
	 */
	

	public Hphone hp = new Hphone();

	private Handphone() {
		super();
		// TODO Auto-generated constructor stub
	}


	public WindowManager manager;

	public Display display;

	private TelephonyManager tm;

	public Rect outSize=new Rect();
	
	static public Handphone handphone = new Handphone();

	private static boolean vmdebug;

	public DisplayMetrics outMetrics=new DisplayMetrics();
 
	
	public static synchronized Handphone getSetting(Context ctx) {
		
		handphone.manager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		handphone.display = handphone.manager.getDefaultDisplay();
		handphone.tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		handphone.hp.deviceid = handphone.tm.getDeviceId(); // 取出ICCID

		handphone.hp.tel = handphone.tm.getLine1Number(); // 取出MSISDN，很可能为空

		handphone.hp.imei = handphone.tm.getSimSerialNumber(); // 取出IMEI

		handphone.hp.imsi = handphone.tm.getSubscriberId(); // 取出IMSI
		if (handphone.hp.deviceid.equals("000000000000000"))
		{	vmdebug = true;
			DebugSetting.debug=  DEBUG.OUTERDEBUG;
		}
		else
			vmdebug = false;
		//vmdebug = false;
		if (DebugSetting.isDebug() ){
			handphone.hp.deviceid = Config.devicid;
			if(  Config.真实模拟id!=null)
				handphone.hp.deviceid = Config.真实模拟id;
		}
		handphone.display.getMetrics(handphone.outMetrics);
		handphone.display.getRectSize(handphone.outSize);
		//MyLog.Log(handphone);
		
		return handphone;

	}
	public static String[] getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		String str2="";
		String[] cpuInfo={"",""};
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return cpuInfo;
	}


	public static boolean isVMDebug() {
		return Handphone.vmdebug;
		
		// TODO Auto-generated method stub
		
	}
	public static boolean isVMIntel() {
		String cpu = Handphone.getCpuInfo()[1];
		return "GenuineIntel".equals(cpu);
		
	}
}

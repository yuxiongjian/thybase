/**
 * 
 */
package project.util;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

/**
 * @author thomasy
 * 
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CustomExceptionHandler";

	private UncaughtExceptionHandler mDefaultUEH;
	private Context ctx;

	public GlobalExceptionHandler(Context ctx) {
		this.ctx = ctx;
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Throwable cs = null;
		// mDefaultUEH.uncaughtException(thread, ex); // 不加本语句会导致ANR
		// StackTraceElement[] stacks = ex.getStackTrace();
		if (ex != null && ex.getCause() != null)
			ex.getCause().printStackTrace();
		// MyLog.Loge(getCrashReport(ex.getCause()));
		else if (ex != null){
			
			while( (cs=ex.getCause())!=null ){
				if ( cs.equals(ex))
					break;
				else ex = cs;
				
			}
			//MyLog.Loge(getCrashReport(ex));
		}
		else
			MyLog.Assert(false, "Throwable is null");
		if ( cs != null)
			ex = cs;
		if ( ex!= null)
		{	
			ex.printStackTrace();
			Toast.makeText(ctx, (CharSequence) ex.getMessage(), Toast.LENGTH_LONG).show();
		
		}
		
		// StackTraceElement[] stacks1 = thread.getStackTrace();
		//android.os.Process.killProcess(android.os.Process.myPid());
		//System.exit(1);
		// mDefaultUEH.uncaughtException(thread, ex);
	}

	private String getCrashReport(Throwable ex) {

		StringBuffer exceptionStr = new StringBuffer();
		if (ctx != null) {
			PackageInfo pinfo = getPackageInfo(ctx);

			exceptionStr.append("version: " + pinfo.versionName + "("
					+ pinfo.versionCode + ")\r\n");
		}
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE
				+ "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\r\n");

		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\r\n");
		}
		return exceptionStr.toString();
	}

	private PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// e.printStackTrace(System.err);
			// L.i("getPackageInfo err = " + e.getMessage());
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

}

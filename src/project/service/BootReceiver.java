/**
 * 
 */
package project.service;

import project.util.MyLog;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;

/**
 * @author thomasy
 * 
 */
public class BootReceiver extends android.content.BroadcastReceiver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */

	@Override
	public void onReceive(Context context, Intent intent) {

		
		// TODO Auto-generated method stub
		MyLog.Log("Start Notify Command");
		// Intent startIntent = new Intent(context, NotifyService.class);  
		// context.startService(startIntent);  
	}

}

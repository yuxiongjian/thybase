/**
 * 
 */
package project.util;

import java.util.Set;

import project.config.Config;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author thomasy
 * 
 */
public class ProjPref {


	public static Context ctx=null;
	
	public static Context getCtx() {
		return ctx;
	}
	public static void setCtx(Context ctx) {
		ProjPref.ctx = ctx;
	}
	public static Object GetPref( String tag, Object defaultObject){
		return GetPref( ctx,  tag,  defaultObject);
	}
	@SuppressWarnings("unchecked")
	public static Object GetPref(Context ct, String tag, Object defaultObject) {
		Object ret = null;

		SharedPreferences configSP = ct.getSharedPreferences(Config.SHAREDPREFERENCES,
				Activity.MODE_PRIVATE);
		if (defaultObject.getClass().isAssignableFrom(String.class))
			ret = configSP.getString(tag, (String) defaultObject);
		else if (defaultObject.getClass().isAssignableFrom(Integer.class))
			ret = configSP.getInt(tag, (Integer) defaultObject);
		else if (defaultObject.getClass().isAssignableFrom(Set.class))
			ret = configSP.getStringSet(tag, (Set<String>) defaultObject);
		else if (defaultObject.getClass().isAssignableFrom(Boolean.class))
			ret = configSP.getBoolean(tag, (Boolean) defaultObject);
		else if (defaultObject.getClass().isAssignableFrom(Long.class))
			ret = configSP.getLong(tag, (Long) defaultObject);
		else if (defaultObject.getClass().isAssignableFrom(Float.class))
			ret = configSP.getFloat(tag, (Float) defaultObject);


		else
			MyLog.Assert(false, defaultObject.getClass().getName() + ":不支持的类型");
		return ret;
	}

	@SuppressWarnings("unchecked")
	
	public static void SavePref(String tag, Object value) {
		 SavePref( ctx,  tag,  value);
	}
	
	public static void SavePref(Context ct, String tag, Object value) {
		Object ret = null;

		SharedPreferences configSP = ct.getSharedPreferences(project.config.Config.SHAREDPREFERENCES,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = configSP.edit();
		if (value.getClass().isAssignableFrom(String.class))
			ret = editor.putString(tag, (String) value);
		else if (value.getClass().isAssignableFrom(Integer.class))
			ret = editor.putInt(tag, (Integer) value);
		else if (value.getClass().isAssignableFrom(Set.class))
			ret = editor.putStringSet(tag, (Set<String>) value);
		else if (value.getClass().isAssignableFrom(Boolean.class))
			ret = editor.putBoolean(tag, (Boolean) value);
		else if (value.getClass().isAssignableFrom(Long.class))
			ret = editor.putLong(tag, (Long) value);
		else if (value.getClass().isAssignableFrom(Float.class))
			ret = editor.putFloat(tag, (Float) value);

		else
			MyLog.Assert(false, value.getClass().getName() + ":不支持的类型");
		editor.commit();
	}

}

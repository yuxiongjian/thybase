/**
 * 
 */
package project.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * @author thomasy
 * 
 */
public class Func {
	static public class TimeSerializer implements JsonSerializer<Time>,
			JsonDeserializer<Time> {
	
		// 对象转为Json时调用,实现JsonSerializer<PackageState>接口
		@Override
		public JsonElement serialize(Time state, Type arg1,
				JsonSerializationContext arg2) {
			return new JsonPrimitive(state.toString());
		}
	
		// json转为对象时调用,实现JsonDeserializer<PackageState>接口
		@Override
		public Time deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			// if (json.getAsInt() < PackageState.values().length)
			// return Time.values()[json.getAsInt()];
	
			@SuppressWarnings("deprecation")
			Time t = Time.valueOf(json.getAsString());
			// Long l = DateFormat.parse(json.getAsString());
			// Time t = new Time(l);
			return t;
		}
	
	}

	static public class Date_TimeSerializer<T> implements JsonSerializer<T>,
			JsonDeserializer<T> {
	
		public Date_TimeSerializer() {
			super();
			genType = getClass().getGenericSuperclass();
	
			// params = ((ParameterizedType) genType).getActualTypeArguments();
			// paramClass = (Class<?>)params[0];
			// TODO Auto-generated constructor stub
		}
	
		Type genType = null;
	
		Type params[];
		Class<?> paramClass;
	
		// 对象转为Json时调用,实现JsonSerializer<PackageState>接口
		@Override
		public JsonElement serialize(T state, Type arg1,
				JsonSerializationContext arg2) {
	
			JsonPrimitive jp = null;
			if( arg1.equals(java.util.Date.class))
				jp = new JsonPrimitive(Func.dateToString((Date)state));
			else if( arg1.equals(java.sql.Date.class))
				jp = new JsonPrimitive(Func.dateToString((java.sql.Date)state));
			else if( arg1.equals(java.sql.Time.class))
				jp = new JsonPrimitive(Func.timeToString((java.sql.Time)state));
			if( arg1.equals(java.sql.Timestamp.class))
				jp = new JsonPrimitive(Func.timestampToString((Timestamp)state));
			return jp;
		}
	
		// json转为对象时调用,实现JsonDeserializer<PackageState>接口
		@SuppressWarnings("unchecked")
		@Override
		public T deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) {
	
			T t = null;
			Class<?> Tclass = (Class<?>) typeOfT;
			try {
				if (json.isJsonNull())
					return null;
				// if ( json.getAsString().length()==0)
				// return null;
	
				if (Tclass.equals(java.util.Date.class)) {
					t = (T) new Date(json.getAsLong());
					// return JsonPrimitive.this.
					// JsonPrimitive js = new JsonPrimitive(Date.class);
					// t = (T) Func.toDate(json.getAsString());
					// t = (T) new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getAsString());
					// t = (T) json.getAsJsonPrimitive();
				} else if (Tclass.isAssignableFrom(java.sql.Date.class))
					t = (T) java.sql.Date.valueOf(json.getAsString());
				else if (Tclass.isAssignableFrom(java.sql.Time.class))
					t = (T) java.sql.Time.valueOf(json.getAsString());
				else if (Tclass.isAssignableFrom(java.sql.Timestamp.class)) {
					// Date t1 = Date.valueOf(json.getAsString());
					t = (T) new Timestamp(json.getAsLong());
					//
				}
	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				MyLog.Loge(json);
				MyLog.False(e);
				t = null;
			}
			// Long l = DateFormat.parse(json.getAsString());
			// Time t = new Time(l);
			return t;
		}
	
	}

	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static final String HH_MM_SS = "HH:mm:ss";
	public static final String HH_MM = "HH:mm";

	public static String dateToString(Date t) {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		return (String) f.format(t);
	}
	
	public static String timestampToString(Timestamp t) {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		return (String) f.format(t);
	}

	public static String timeToShortString(Time t) {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(HH_MM);
		return (String) f.format(t);
	}
	public static String timeToString(Time t) {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(HH_MM_SS);
		return (String) f.format(t);
	}
	
	public static String dateToString(java.sql.Date t) {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(YYYY_MM_DD);
		return (String) f.format(t);
	}
	
	public static Date toDate(String t) throws ParseException {
		if( t==null)
			return null;
		SimpleDateFormat f = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
		return f.parse(t);
	}

	

	public static Object toSQLDate(String t) {
		// TODO Auto-generated method stub
		if( t==null)
			return null;
		java.sql.Date d =null;
		return java.sql.Date.valueOf(t);
		
	}
	
	public static Object toSQLTime(String t)  {
		// TODO Auto-generated method stub
		if( t==null)
			return null;
		
		java.sql.Time ti = null;
		ti = Time.valueOf(t);
		
		return ti;
		
	}
	
	public static StringBuffer ReadResourceFile(Context ctx, int resid) {

		InputStream myFile = null;
		StringBuffer ret = new StringBuffer("");
		myFile = ctx.getResources().openRawResource(resid);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(myFile, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			MyLog.Loge(e1.toString());

		}
		String tmp;
		try {
			while ((tmp = br.readLine()) != null) {
				ret.append(tmp);
				// 对tmp的一些解析操作

			}
			br.close();
			myFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("debug", e.toString());

		}

		return ret;
	}

	public static Gson createGson() {
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
		// gb.registerTypeAdapter(Time.class, new TimeSerializer());
		gb.registerTypeAdapter(java.sql.Date.class,
				new Func.Date_TimeSerializer<java.sql.Date>());
		gb.registerTypeAdapter(java.sql.Time.class,
				new Func.Date_TimeSerializer<java.sql.Time>());
		gb.registerTypeAdapter(java.sql.Timestamp.class,
				new Func.Date_TimeSerializer<java.sql.Timestamp>());
		gb.registerTypeAdapter(java.util.Date.class,
				new Func.Date_TimeSerializer<java.util.Date>());
		//gb.setPrettyPrinting();
		//gb.excludeFieldsWithoutExposeAnnotation();
		
		gb.excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, 
				Modifier.VOLATILE,Modifier.FINAL,Modifier.PRIVATE);
		Gson gs = gb.create();
		return gs;
	
	}

	public static String toString(String str) {
		// TODO Auto-generated method stub
		if (str==null)
			return "";
		else 
		return str;
	}

	public static void makeCall(Activity act,String mobile) {
		// TODO Auto-generated method stub
		
		//if (PhoneNumberUtils.isDialable(mobile))
		{
			
			Intent i = new Intent();
			i.setAction(Intent.ACTION_DIAL);
			i.setData(Uri.parse("tel:" + mobile));
			
			act.startActivity(i);
		} /*else {
			Toast.makeText(act, R.string.message_not_mobile_number, 5000).show();
		}*/
		
	}

	public static String getString(String s) {
		if (s==null)
			return "";
		return s;
		// TODO Auto-generated method stub
		
	}

}

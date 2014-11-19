/**
 * 
 */
package project.util;

import java.io.Serializable;

import org.alexd.jsonrpc.JSONRPCException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.util.Log;

/**
 * @author thomasy
 * 
 */
public class MyLog {

	private static Gson gson = new Gson();

	public static String getLineStr() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = new Throwable().getStackTrace();
		int stacksLen = stacks.length;
		int i = 0;
		try {

			while (i < stacksLen) {
				Class<?> o = Class.forName(stacks[i].getClassName());

				if (!(MyLog.class).isAssignableFrom(o))
					break;
				i++;
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StackTraceElement stack = stacks[i];
		sb.append("at ")
				// .append(stack.getClassName()).append(".").append(stack.getMethodName())
				.append("(").append(stack.getFileName()).append(":")
				.append(stack.getLineNumber()).append(")");

		return sb.toString();

	}

	public static String toString(Object... ss) {
		StringBuffer out = new StringBuffer("");

		if (ss == null)
			out = out.append("null");
		else {
			for (Object s : ss) {
				if( out.length()>0)
					out.append("\r\n");
				if (s == null)
					out.append("null");
				else if (s.getClass().equals(JSONObject.class)
						|| s.getClass().equals(JSONArray.class)
						||s.getClass().equals(JSONRPCException.class))
					out.append(s.toString());
				else if (!Serializable.class.isAssignableFrom(s.getClass()))
					out.append(s.toString());
				else
					out.append(gson.toJson(s));
			}
		}
		return out.toString();

	}

	public static void Log(Object... s) {

		Log.i("thy" + " " + getLineStr(), toString(s));
	}

	public static void Loge(Object ...s) {

		Log.e("thy" + " " + getLineStr(), toString(s));
	}

	public static void Assert(boolean s, Object msg) {

		if (s == false) {

			if (msg != null && Exception.class.isAssignableFrom(msg.getClass()))
				((Exception) msg).printStackTrace();
			Log.wtf(getLineStr(), toString(msg));
			throw new RuntimeException();
			
		}

		// Log.ASSERT(getStr(), s.toString());
		return;
	}

	public static void False(Object msg) {

		// if ( s == true)
		Assert(false, msg);
		// Log.ASSERT(getStr(), s.toString());

	}

	public static void AssertFalse(Boolean s, Object msg) {

		// if ( s == true)
		Assert(!s, msg);
		// Log.ASSERT(getStr(), s.toString());

	}

}

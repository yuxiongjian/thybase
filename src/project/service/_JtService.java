package project.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.alexd.jsonrpc.JSONRPCHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import project.config.Config;
import project.config.DebugSetting;
import project.util.Func;
import project.util.MyLog;
import project.util.ProjectThread;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class _JtService {

	public static final int CONNECTION_TIMEOUT = 10000;
	public static final int SO_TIMEOUT = 10000;
	public static final String leaveForm = "";
	protected static String ServiceUrl = Config.ServiceUrl;
	protected static String _diServiceUrl = Config._diServiceUrl;
	protected static String _doServiceUrl = Config._doServiceUrl;
	protected static String SID = "";
	public static Context ctx ;

	public static Object CallJtService(Class<?> retclass, String fun,
			Object... param) throws InterruptedException, JSONRPCException {
		return _CallJtService(null,retclass, null, fun, param);
	}

	public static Object _CallJtService(MyHandler outerHandler, Class<?> retclass, String jsonCaller,
			String fun, Object... param) throws InterruptedException, JSONRPCException {

		ServiceThread st = null;
		MyHandler myHandler = outerHandler;
		try {
			
			
			//MyLog.Assert(ctx!=null, "Jtservice ctx 未初始化");
			
			/*if ( ctx != null )
				myHandler = new MyHandler(ctx.getMainLooper());
			else{
				//Looper.prepare();
			    myHandler = new MyHandler();
			}*/
			st = new ServiceThread(myHandler, retclass, jsonCaller, fun, param);
			st.start();

			st.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			throw e;
		}
		if (st.serviceExp != null)
			throw st.serviceExp;
		else 	{
			if(retclass != null && st.serviceRet!=null && !st.serviceRet.getClass().equals(retclass)){
				MyLog.False("JsonCall 返回类型错误:"+ fun+",retclass:"+ st.serviceRet.getClass().getName() );
				return null;
			}else
			return st.serviceRet;
		}

	}

	static Object hmo[][] = { { Integer.class, "callInt" },
			{ Boolean.class, "callBoolean" }, { Long.class, "callLong" },
			{ Double.class, "callDouble" }, { Float.class, "callDouble" },
			{ String.class, "callString" },
			{ Object[].class, "callJSONArray" }, { Void.class, "call" }
	// ,{Object.class, "callJSONObject"}
	};

	public static Object requestCall(Class<?> c, String caller, String func,
			Object... param) throws JSONRPCException {
		Object retObj;

		@SuppressWarnings("unused")
		int result = 0;
		Object ret = null;
		Method callMd = null;
		
		String caller2 = null;
		try {

			JSONRPCHttpClient jRequest = createConnection();
			//mds = JSONRPCClient.class.getDeclaredMethods();
			for (int i = 0; i < hmo.length; i++)
				if (c.isAssignableFrom((Class<?>) hmo[i][0])) {
					caller2 = (String) hmo[i][1];
					break;
				}
			if (c.isArray())
				caller2 = "callJSONArray";
			if (caller2 == null)
				caller2 = "callJSONObject";
			if (caller == null)
				caller = caller2;
			//MyLog.Assert(caller.equals(caller2), "");
			//MyLog.Assert(caller!=null, "RequectCall error");
			callMd = JSONRPCClient.class.getDeclaredMethod(caller,
					String.class, Object[].class);
			// t = mds[0].getGenericReturnType();

			retObj = callMd.invoke(jRequest, func, param);
			if (retObj == null)
				return null;
			Gson gs = Func.createGson();

			try {
				project.pojo.Bpojo.verifyJson(c, retObj);

				ret = gs.fromJson(retObj.toString(), c);
			} catch (JsonSyntaxException e) {
				JSONRPCException erpc =new JSONRPCException("Invalid Json return Format:"+e.getMessage());
				ret = null;
				throw erpc;
				
				//MyLog.False(e);
			}
			// {"userid":"develop","sid":"eyJleHBpcmUiOjEzOTc0NTQ5MTI2NDQsInVzZXJpZCI6ImRldmVsb3AifQ==","username":"develop","displayName":"develop","mobile":{},"deviceid":"863034021964403","imei":{}}
			return ret;

		} catch (NoSuchMethodException e) {
			JSONRPCException erpc =new JSONRPCException("NoSuchMethodException Json call :"+func);
			ret = null;
			throw erpc;
		} catch (IllegalArgumentException e) {
		
			JSONRPCException erpc =new JSONRPCException("IllegalArgumentException Json call :"+func);
			ret = null;
			throw erpc;
		} catch (IllegalAccessException e) {
			MyLog.False(e);
		} catch (InvocationTargetException e) {
			Throwable ecause = e.getCause();
			if (ecause != null
					&& ecause.getClass().isAssignableFrom(
							JSONRPCException.class)) {
				JSONRPCException jecause = (JSONRPCException) ecause;
				JSONObject jErr;
				try {

					jErr = new JSONObject(jecause.getMessage());
					jecause.errCode = jErr.optInt("code",0);
					
					throw  jecause;
				} catch (JSONException e1) {

					throw (JSONRPCException) ecause;// 必须返回 e，而不是 e1;
				}

			}else
			{
				MyLog.Log(e);
			}
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {

		}
		return ret;

	}

	protected static class ServiceThread extends ProjectThread {

		private String func;
		private Object[] param;
		private MyHandler mh;
		private Class<?> retClass;
		Object serviceRet;
		JSONRPCException serviceExp;
		private String caller;

		ServiceThread(MyHandler mh, String func, Object param[]) {
			this.func = func;
			this.param = param;
			this.mh = mh;
		}

		ServiceThread(MyHandler mh, Class<?> retClass, String caller,
				String func, Object param[]) {
			this.caller = caller;
			this.func = func;
			this.param = param;
			this.mh = mh;
			this.retClass = retClass;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized (this) {
			//	 Looper.prepare();
			//	Message msg = mh.obtainMessage(1001);
			//	msg.sendToTarget();
				try {
					if (this.retClass == null)
						MyLog.False("NOt support");
					// serviceRet = _CallJtService(func, param);
					else
						serviceRet = _JtService.requestCall(retClass, caller,
								func, param);
					
				} catch (JSONRPCException e) {
					
					serviceExp = (JSONRPCException) e;
					
				}

			}
		};
	}

	protected static class MyHandler extends Handler {
		Integer mutex;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			//Thread thread = Thread.currentThread();
			//Log.i("thy-thread","JtServiceHandle:handleMessage:threadid="+thread.getId()+",what="+msg.what);
			super.handleMessage(msg);;

		}

		public MyHandler() {
			super();
			//Thread thread = Thread.currentThread();
			//Log.i("thy-thread","JtServiceHandle:MyHandler:threadid="+thread.getId());
		}

		public MyHandler(Callback callback) {
			super(callback);
			// TODO Auto-generated constructor stub
		}

		public MyHandler(Looper looper, Callback callback) {
			super(looper, callback);
			// TODO Auto-generated constructor stub
		}

		public MyHandler(Looper looper) {
			super(looper);
			// TODO Auto-generated constructor stub
		}

	}



	/**
	 * @return the serviceUrl
	 */
	public static String getServiceUrl() {
		if (DebugSetting.isRelease())
			return ServiceUrl;
		else if (DebugSetting.isInnerDebug())
			return _diServiceUrl;
		else if (DebugSetting.isOuterDebug())
			return _doServiceUrl;
		else if (DebugSetting.isLocalDebug())
			return ServiceUrl;

		return ServiceUrl;
	}

	/**
	 * @param serviceUrl
	 *            the serviceUrl to set
	 */
	public static void setServiceUrl(String serviceUrl) {
		ServiceUrl = serviceUrl;
	}

	/**
	 * @return the sID
	 */
	public static String getSID() {
		return SID;
	}

	static JSONRPCHttpClient createConnection() throws JSONRPCException {

		JSONRPCHttpClient jRequest = new JSONRPCHttpClient(
				getServiceUrl());
		MyLog.Log("Service URL:"+getServiceUrl());
		jRequest.setDebug(true);
		jRequest.setConnectionTimeout(CONNECTION_TIMEOUT);
		if( !project.util.NetworkTool.isNetworkConnected(ctx))
			throw new JSONRPCException("请检查网络是否连接");
		return jRequest;

	}


}

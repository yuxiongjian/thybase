/**
 * 
 */
package project.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import project.config.Config;
import project.config.DebugSetting;
import project.pojo.Bpojo;
import project.pojo.IUser;
import project.pojo.User;

import project.util.CommitClick;
import project.util.GlobalExceptionHandler;
import project.util.MyLog;
import project.util.ProjectLocationListener;
import project.util.zxing.act.CaptureActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.thybase.R;

/**
 * @author thomasy
 * 
 */
public class ProjectActivity extends Activity implements Serializable,
		OnFocusChangeListener, TextWatcher, BTCallBack, OnGestureListener,
		OnTouchListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = ProjectActivity.class
			.hashCode();
	protected CommitClick ClickEx;
	public MyHandler msgHandler;
	{msgHandler= new MyHandler(null, null);}
	protected ProjectActivity thisAct;
	protected Button btcancel;
	/**
	 * @return the msgHandler
	 */
	public  MyHandler getMsgHandler() {
		return msgHandler;
	}

	/**
	 * @param msgHandler the msgHandler to set
	 */
	public  void setMsgHandler(MyHandler msgHandler) {
		this.msgHandler = msgHandler;
	}

	protected Button btok;
	protected TabInitData tabInitData;
	protected Resources mResource;
	/**
	 * 维护本Form中所有的可视控件的Map，以Pojo的FieldName为索引，值为 {@link viewHolder}
	 */
	protected HashMap<String, Bpojo.ViewHolder> mViewMap;//
	/**
	 * 维护本Form中所有的动态可视控件的Map，以Pojo的FieldName为索引，值为 {@link viewHolder}
	 */
	protected ArrayList<HashMap<String, Bpojo.ViewHolder>> mViewMaps;//
	protected ProjectTableForm tableForm;
	protected Bpojo formPojo;
	protected Bpojo formPojos[];
	// protected HashMap<String, ViewHolder> viewMaps;
	private IUser au = User.getUser();
	public HashSet<Integer> checkIDs = new HashSet<Integer>();
	private boolean bWatchble;
	protected GestureDetector gestureDetector;

	// public abstract long getRequestCode();
	public class MyRunnable implements Runnable {
		protected Object[] objs = {};

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

		public MyRunnable(Object... objs) {
			this.objs = objs;
			// TODO Auto-generated constructor stub
		}

	}

	public int getListCount() {
		return 0;
	}

	public class MyHandler extends Handler {
		Integer mutex;
		View myView;

		public MyHandler(Integer mutex, View myview) {
			super();
			this.mutex = mutex;
			this.myView = myview;
		}

		public MyHandler() {
			this(null, null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			handleProjectMessage(msg);

		}

	}

	public void handleProjectMessage(Message msg) {
		// TODO Auto-generated method stub
		//MyLog.Log("handleProjectMessage:"+this.getClass().getName());
		if (msg.what == ProjectLocationListener.class.hashCode())
			handleLocationMsg(msg.obj);

	}

	public void handleLocationMsg(Object l) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		thisAct = this;
		mResource = thisAct.getResources();
		tableForm = new ProjectTableForm();

		Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(
				this));

	}

	public List<View> getAllChildViews() {
		View view = this.getWindow().getDecorView();
		return getAllChildViews(view);
	}

	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if (view instanceof ViewGroup) {
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				View viewchild = vp.getChildAt(i);
				allchildren.add(viewchild);
				allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		ClickEx = new CommitClick(this);
		btcancel = (Button) findViewById(R.id.BT_CANCEL);
		if (btcancel != null)
			btcancel.setOnClickListener(ClickEx);
		btok = (Button) findViewById(R.id.BT_OK);
		if (btok != null)
			btok.setOnClickListener(ClickEx);
		// setOnTouchListener(this);
		gestureDetector = new GestureDetector(this, this);
		if (DebugSetting.isDebug())
			setTitle(getTitle() + "-测试系统");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return this.gestureDetector.onTouchEvent(event);
	}

	public void OnOK(View v) {
	}

	public void OnCancel(View v) {
	}

	public HashMap<String, Bpojo.ViewHolder> getViewMap() {
		return mViewMap;
	}

	public void setViewMap(HashMap<String, Bpojo.ViewHolder> hashMap) {
		this.mViewMap = hashMap;
	}

	public Bpojo getFormPojo() {
		return formPojo;
	}

	public void setFormPojo(Bpojo formPojo) {
		this.formPojo = formPojo;
	}

	public HashMap<String, String> refreshData(HashMap<String, String> retMap) {

		return getFormPojo().refreshData(getViewMap(), retMap);
		// TODO Auto-generated method stub

	}

	public HashMap<String, String> refreshData(Bpojo b,
			HashMap<String, Bpojo.ViewHolder> viewMap, HashMap<String, String> retMap) {

		return b.refreshData(viewMap, retMap);
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> refreshDatas(HashMap<String, String> retMap) {

		HashMap<String, Bpojo.ViewHolder> h = new HashMap<String, Bpojo.ViewHolder>();
		HashMap<String, Bpojo.ViewHolder>[] vms;
		// = new HashMap<String, ViewHolder> ;
		// HashMap<String, ViewHolder>[0]
		if (getViewMaps() != null)
			vms = (HashMap<String, Bpojo.ViewHolder>[]) (getViewMaps()
					.toArray(new HashMap[0]));
		else
			return retMap;
		return refreshDatas(vms, retMap);
	}

	public HashMap<String, String> refreshDatas(
			HashMap<String, Bpojo.ViewHolder> viewMaps[],
			HashMap<String, String> retMap) {

		Bpojo b;
		HashMap<String, Bpojo.ViewHolder> viewMap;
		for (int i = 0; i < viewMaps.length; i++) {

			b = formPojos[i];
			viewMap = viewMaps[i];
			retMap = b.refreshData(viewMap, retMap);

		}
		return retMap;
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	public IUser getAu() {
		return au;
	}

	public void setAu(IUser au) {
		this.au = au;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) { // hasFocus表示是否获得焦点

	}

	public static class ThyTextWatcher implements TextWatcher {
		View mView = null;
		private ProjectActivity pAct;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

			pAct.beforeTextChanged(mView, s, start, count, after);
			// TODO Auto-generated method stub

		}

		public ThyTextWatcher(View mView, ProjectActivity act) {
			super();
			this.mView = mView;
			this.pAct = act;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			pAct.onTextChanged(mView, s, start, before, count);
			// Log.d("TAG","[TextWatcher][onTextChanged]"+s);

		}

		@Override
		public void afterTextChanged(Editable s) {
			pAct.afterTextChanged(mView, s);
			// MyLog.Log(s);
			// TODO Auto-generated method stub

		}

	}

	public void afterTextChanged(View mView, Editable s) {
		// TODO Auto-generated method stub
		MyLog.Log(mView.getTag("fieldname".hashCode()));

	}

	public void beforeTextChanged(View mView, CharSequence s, int start,
			int count, int after) {
		// TODO Auto-generated method stub

	}

	public void onTextChanged(View mView, CharSequence s, int start,
			int before, int count) {

	}

	public boolean isWatchable() {

		return bWatchble;
	}

	/**
	 * @return the bWatchble
	 */
	public boolean isbWatchble() {
		return bWatchble;
	}

	/**
	 * @param bWatchble
	 *            the bWatchble to set
	 */
	public void setbWatchble(boolean bWatchble) {
		this.bWatchble = bWatchble;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public ArrayList<HashMap<String, Bpojo.ViewHolder>> getViewMaps() {
		return mViewMaps;
	}

	public void setViewMaps(ArrayList<HashMap<String, Bpojo.ViewHolder>> viewMaps) {
		this.mViewMaps = viewMaps;
	}

	public Bpojo[] getFormPojos() {

		return this.formPojos;
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		return;
	}

	@Override
	public boolean onDown(MotionEvent e) {

		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		try {

			ProjectTabHostActivity act = (ProjectTabHostActivity) this.tabInitData.rootAct;
			if (act != null)
				return act.onFling(e1, e2, velocityX, velocityY);

		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

		return;

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return this.onTouchEvent(event);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == (int) CaptureActivity.serialVersionUID) {
			EditText eevt = (EditText) findViewById(R.id.scantext);
			String bc="";
			if (resultCode == Activity.RESULT_OK && data!=null) {
				bc = data.getStringExtra("barcode");
				if (eevt != null)
					eevt.setText(bc);
			} else if (resultCode == Activity.RESULT_CANCELED) {

				if (DebugSetting.isDebug()) {
					bc = Config.BARCODE;
					if (eevt != null)
						eevt.setText(bc);
				}

			}

		}
	}
}

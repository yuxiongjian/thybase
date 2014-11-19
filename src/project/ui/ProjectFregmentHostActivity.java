package project.ui;

import java.io.Serializable;

import project.config.DebugSetting;
import project.util.CommitClick;

import com.thybase.R;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;


public class ProjectFregmentHostActivity extends FragmentActivity implements
		OnGestureListener, Serializable, BTCallBack, OnTabChangeListener,
		OnTouchListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = ProjectFregmentHostActivity.class
			.hashCode();
	private static final int FLEEP_DISTANCE = 350;
	private static final int FLEEP_SPEED = 2500;
	public FragmentTabHost mTabHost;
	private TabWidget mTabWidget;
	private ImageView cursor;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	//private ImageView[] views;
	private Resources mResource;
	private CommitClick ClickEx;
	private Button btcancel;
	private Button btok;

	public SparseArray<TabInitData> tabdata = new SparseArray<TabInitData>();
	private OnTabChangeListener thisAct;
	private GestureDetector gestureDetector;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main_hometabhost);

		// SystemScreenInfo.getSystemInfo(HomeTabHostAcitivity.this);
		thisAct = this;
		mResource = getResources();
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);  
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);   
       // mTabWidget = mTabHost.getTabWidget();
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		//mTabHost = (AnimationFregmentTabHost) findViewById(android.R.id.tabhost);

		onInitTabs();
		InitImageView();
	}

	public void onTabChanged(String tabId) {
		int tabID = Integer.valueOf(tabId);
		// views[currIndex].setImageResource(getImageId(currIndex,
		// false));
		// views[tabID].setImageResource(getImageId(tabID, true));
		onPageSelected(tabID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.TabActivity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle icicle) {
		mTabWidget.setDividerDrawable(R.drawable.tab_vline);
		super.onPostCreate(icicle);

		ClickEx = new CommitClick(this, this.getParent());
		btcancel = (Button) findViewById(R.id.BT_CANCEL);
		if (btcancel != null)
			btcancel.setOnClickListener(ClickEx);
		btok = (Button) findViewById(R.id.BT_OK);
		if (btok != null)
			btok.setOnClickListener(ClickEx);

		//initBottomMenu();
		mTabHost.setOnTabChangedListener(thisAct);
		gestureDetector = new GestureDetector(getBaseContext(), this);
		if ( DebugSetting.isDebug())
			setTitle(getTitle()+"-测试系统");
	}

	/*
	private void initBottomMenu() {
		int viewCount = mTabWidget.getChildCount();
		views = new ImageView[viewCount];
		
		 for (int i = 0; i < views.length; i++) {
		  View v = (LinearLayout) mTabWidget.getChildAt(i); 
		  views[i] = (ImageView) v.findViewById(R.id.main_activity_tab_image); 
		  }
		 

	}*/

	protected int mLastSelect;
	private int currentTabID;

	protected void onInitTabs() {

	}

	@SuppressWarnings("unused")
	public void updateTab() {
		
		for (int i = 0; i < tabdata.size(); i++) {
			int key = tabdata.keyAt(i);
			TabInitData iddata = tabdata.get(key);

			if (iddata == null)
				continue;
			ProjectActivity act = (ProjectActivity) iddata.tabActivity;
			if ( act == null)
				continue;
			TextView tv =  iddata.tv;
			if ( tv == null )
				continue;
			String text = tv.getText().toString();
			int count = act.getListCount();
			String regularExpression = "\\(\\d+\\)";
			String add=  "(" + count + ")";
			text.replaceAll(regularExpression,add);
			if (text.indexOf(add)==-1)
				text = text+add;
			tv.setText(text);
		
		}
	}

	protected void setIndicator(int ssid, int tabId, Intent intent, int image_id) {

		View localView = LayoutInflater.from(this.mTabHost.getContext())
				.inflate(R.layout.tab_widget_view, null);
		TextView tv;
		TabInitData t0 = new TabInitData(this);
		t0.tabid = tabId;
		
		tabdata.put(tabId, t0);
		intent.putExtra("" + TabInitData.serialVersionUID, t0);

		tv = ((TextView) localView.findViewById(R.id.main_activity_tab_text));
	//	tv.setPadding(2, 0, 2, 0);
		tv.setText(ssid);
		t0.tv = tv;
		if (image_id > 0)
			tv.setTextColor(mResource.getColor(R.color.tabselected));

		String str = String.valueOf(tabId);
		TabSpec localTabSpec = mTabHost.newTabSpec(str)
				.setIndicator(localView);//.setContent(intent);
		
		mTabHost.addTab(localTabSpec,ProjectFragment.class,null);
	}

	private void InitImageView() {

		cursor = (ImageView) findViewById(R.id.cursor);
		// cursor.setAlpha(0.8);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.main_tab_anim_light).getWidth();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / this.mTabWidget.getTabCount() - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);
	}

	public void onPageSelected(int arg0) {

		int one = offset * 2 + bmpW;
		Animation animation = null;
		animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);
		currIndex = arg0;
		animation.setFillAfter(true);
		animation.setDuration(300);
		cursor.startAnimation(animation);
		TextView tv = (TextView) mTabWidget.getChildAt(mLastSelect)
				.findViewById(R.id.main_activity_tab_text);
		tv.setTextColor(Color.WHITE);
		tv = (TextView) mTabWidget.getChildAt(arg0).findViewById(
				R.id.main_activity_tab_text);
		tv.setTextColor(mResource.getColor(R.color.tabselected));
		mLastSelect = arg0;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		}
		return false;
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() <= (-FLEEP_DISTANCE) && Math.abs(velocityX)>FLEEP_SPEED) {// 从左向右滑动
			currentTabID = mTabHost.getCurrentTab() - 1;
			if (currentTabID < 0) {
				currentTabID = mTabHost.getChildCount() - 1;
			}
		} else if (e1.getX() - e2.getX() >= FLEEP_DISTANCE && Math.abs(velocityX)>FLEEP_SPEED) {// 从右向左滑动
			currentTabID = mTabHost.getCurrentTab() + 1;
			if (currentTabID >= mTabHost.getChildCount()) {
				currentTabID = 0;
			}
		}else 
			return false;
		
		if(currentTabID!=mTabHost.getCurrentTab()  )
			mTabHost.setCurrentTab(currentTabID);
		return false;

	}

	@Override
	public void onLongPress(MotionEvent e) {

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
	public void OnOK(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnCancel(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

}
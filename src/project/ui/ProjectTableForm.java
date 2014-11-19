package project.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

import com.thybase.R;

import project.annotation.ui.PojoUI;
import project.pojo.Bpojo;
import project.pojo.Bpojo.KEYVALUE;
import project.pojo.Bpojo.ValuePosition;
import project.ui.datetimepicker.DateTimePickerDialog;
import project.ui.tuya.TuYaView;
import project.util.CommitClick;
import project.util.Func;
import project.util.MyLog;
import project.util.zxing.act.CaptureActivity;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ProjectTableForm {

	CommitClick textClick = null;

	static public class TableFormLayout implements Cloneable {
		public int tableFormID;
		public boolean singleRow = false;
		int mg = 1;

		public TableFormLayout() {
			super();

			txtl.setMargins(mg, mg, mg, mg);
			// txtl.weight=0.3f;
			txtl.gravity = Gravity.CENTER + Gravity.FILL;

			etxtl.setMargins(mg, mg, mg, mg);
			etxtl.gravity = Gravity.CENTER + Gravity.FILL;
			// etxtl.weight=0.7f;
			// vtxtl.width = TuYaView.w;
			// vtxtl.height = TuYaView.h;
			vtxtl.gravity = Gravity.CENTER + Gravity.FILL;
			vtxtl.setMargins(mg, mg, mg, mg);
			// vtxtl.weight=0.7f;

			wtxtl.gravity = Gravity.CENTER + Gravity.FILL;
			wtxtl.setMargins(mg, mg, mg, mg);
			// wtxtl.weight=0.7f;
		}

		public TableFormLayout(TableFormLayout tf) {

			this.singleRow = tf.singleRow;
			this.tbl = tf.tbl;
			this.etxtl = tf.etxtl;
			this.vtxtl = tf.vtxtl;
			this.wtxtl = tf.wtxtl;
			this.tableFormID = tf.tableFormID;

		}

		// TODO Auto-generated constructor stub

		public TableRow.LayoutParams tbl = new TableRow.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
		public TableRow.LayoutParams txtl = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.MATCH_PARENT);
		public TableRow.LayoutParams etxtl = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);

		public TableRow.LayoutParams vtxtl = new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT);

		public TableRow.LayoutParams wtxtl = new TableRow.LayoutParams(
				TableRow.LayoutParams.MATCH_PARENT,
				TableRow.LayoutParams.MATCH_PARENT);

		int row = 3;
	}

	public static WebView initWebView(Activity act, String url) {

		WebView tv = new WebView(act);

		tv.loadUrl(url);
		setAutoFit(act, tv);

		return tv;
		// tv.setVisibility(View.INVISIBLE);

	}

	public static void setAutoFit(Activity act, WebView webview) {
		WebSettings webSettings = webview.getSettings();
		webSettings.setSupportZoom(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setBuiltInZoomControls(true);// support zoom
		webSettings.setPluginState(WebSettings.PluginState.ON);// support flash
		webSettings.setUseWideViewPort(true);// 關鍵點
		webSettings.setLoadWithOverviewMode(true);

		WindowManager mWm = (WindowManager) act
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		mWm.getDefaultDisplay().getMetrics(metrics);
		int mDensity = metrics.densityDpi;
		// DebugLog.d(TAG, "densityDpi = " + mDensity);
		if (mDensity == 240) {
			webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else if (mDensity == 160) {
			webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		} else if (mDensity == DisplayMetrics.DENSITY_TV) {
			webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		}

	}
	
	/**
	 * 
	 * **/
	public static ArrayList<HashMap<String, Bpojo.ViewHolder>> initFormLayout(
			final ProjectActivity act, int tableFormID, Bpojo FormObjects[],
			TableFormLayout tf1) {
		HashMap<String, Bpojo.ViewHolder> mFieldViewMap = new HashMap<String, Bpojo.ViewHolder>();
		ArrayList<HashMap<String, Bpojo.ViewHolder>> hm = new ArrayList<HashMap<String, Bpojo.ViewHolder>>();
		for (Bpojo b:FormObjects){
			mFieldViewMap = initFormLayout( act,  tableFormID,  b, tf1);
			hm.add(mFieldViewMap);
		}
		return hm ;
	}
	
	
	public static HashMap<String, Bpojo.ViewHolder> initFormLayout(
			final ProjectActivity act, int tableFormID, Bpojo FormObject,
			TableFormLayout tf1) {
		HashMap<String, Bpojo.ViewHolder> mFieldViewMap = new HashMap<String, Bpojo.ViewHolder>();
		TreeMap<String, PojoUI> mPojoUImap = null;
		Resources mRes;
		TableLayout table = (TableLayout) act.findViewById(tableFormID);
		mRes = act.getResources();
		MyLog.Assert(table!=null, "tableFormID:"+tableFormID+",没找到。");
		// TableRow.LayoutParams edviewLayout = tf.etxtl;
		// tf.tableFormID = tableFormID;
		//table.setStretchAllColumns(true);// 必须要有才能符合屏幕大小
		table.setBackgroundColor(Color.WHITE);
		MyLog.Log("FormObject：" + FormObject.getClass());
		MyLog.Log(FormObject);
		mPojoUImap = FormObject.getFieldsAnnotation(PojoUI.class, null);
		if (mPojoUImap == null)
			return null;
		Object[] fieldNamSet = mPojoUImap.keySet().toArray();

		// String[] fieldset = (String[]) o;
		int i = 0, lastline = -1;
		Field fd = null;
		TableRow tablerow = null;
		Bpojo.ViewHolder vh = null;

		for (i=0; i < fieldNamSet.length; i++) {

			
			String fdname = (String) fieldNamSet[i];

			PojoUI pojoUI = mPojoUImap.get(fdname);
			//Object fieldValue = FormObject.getFieldValue(fdname);

			// Object vids[] = new Object[ObjectSize];
			vh = new Bpojo.ViewHolder();
			//Type fieldType = null;

			try {
				fd = FormObject.getClass().getDeclaredField(fdname);
				//fieldType = fd.getType();
			} catch (NoSuchFieldException e1) {
				
				MyLog.False(e1);
			}
			vh.pojoUI = pojoUI;
			// vids[2] = pojoUI;
			// if ( fieldValue == null )
			// fieldValue = "";
			int orderLine = pojoUI.PojoUIOrder() / 10;
			if (orderLine > lastline ||pojoUI.SingleRow()) {

				if (tablerow != null)
					table.addView(tablerow);
				tablerow = new TableRow(act);
				// tablerow.setLayoutParams(tf.tbl);
				tablerow.setBackgroundColor(mRes.getColor(R.color.gray));
				lastline = orderLine;

			}

			TextView vt = null;
			if (pojoUI.PojoUIName().length() > 0)
				vt = initLeftTextView(act, tf1, pojoUI);
			View evt = null;
			String sEditor = pojoUI.PojoEditor();
			if (sEditor == null || sEditor.length() == 0)
				continue;

			evt = initContentView(  act,mFieldViewMap, tf1, FormObject, fdname 	);
			//evt = initContentView(act, mFieldViewMap, mPojoUImap, tf1, pojoUI,
				//	fdname, fieldValue, fieldType, FormObject.getEditType());
			

			vh.tv = vt;
			if (evt!= null && evt.getTag()!=null)
				vh.ev = (View) evt.getTag();// 当显示的view和 editor 不统一时,使用正确的editor
			else vh.ev = evt;
			
			if (evt == null)
				continue;

			if (table.getChildCount() % 2 == 1) {
				if (vt != null)
					vt.setBackgroundColor(mRes.getColor(R.color.silver));
				evt.setBackgroundColor(mRes.getColor(R.color.silver));
			} else {
				if (vt != null)
					vt.setBackgroundColor(Color.WHITE);
				evt.setBackgroundColor(Color.WHITE);
			}
			MyLog.Assert(tablerow != null, "table Row 没有初始化");

			// tablerow.addView(vt, tf.txtl);
			if (vt != null)
				tablerow.addView(vt);
			if (pojoUI.SingleRow()) {

				table.addView(tablerow);
				tablerow = new TableRow(act);

				tablerow.setBackgroundColor(Color.GRAY);

			}

			tablerow.addView(evt);
			
			mFieldViewMap.put(fdname, vh);

		}
		table.addView(tablerow);

		//MyLog.Log("Form Layout：" + mFieldViewMap);
		return mFieldViewMap;
	}
	private static View initContentView( ProjectActivity act,
			HashMap<String, Bpojo.ViewHolder> ret_mFieldViewMap,TableFormLayout tf1,
			Bpojo formObject,String fdname 	) {
		
		try {
			return initContentView(act,ret_mFieldViewMap,formObject.getFieldsAnnotation(),tf1,formObject.getFieldAnnotation(fdname), fdname,
					formObject.getFieldValue(fdname), formObject.getClass().getDeclaredField(fdname).getType(), formObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	
	private static View initContentView(final ProjectActivity act,
			HashMap<String, Bpojo.ViewHolder> mFieldViewMap,
			TreeMap<String, PojoUI> mPojoUImap, TableFormLayout tf1,
			PojoUI pojoUI, String fdname, Object fieldValue, Type fieldType,
			Bpojo formObject) {

		TableFormLayout tf= tf1;
		if (tf1 == null)
			tf = new TableFormLayout();
		else
			tf = new TableFormLayout(tf1);
		String sEditor = pojoUI.PojoEditor();
		if (sEditor == null || sEditor.length() == 0)
			return null;
		View evt;
		tf.etxtl.span = pojoUI.Span();

		if (sEditor.equals("TextView")) {

			evt = initTextView(act, tf.etxtl, fieldValue, fieldType, pojoUI);

		} else if (sEditor.equals("EditText")) {

			evt = initEditTextView(act, tf.etxtl, fieldValue, fieldType, pojoUI);

		} else if (sEditor.equals("ScanText")) {

			evt = initScannerView(act, tf.etxtl, fieldValue, fieldType, pojoUI);

		} else if (sEditor.equals("Spinner")) {
			
			Spinner eevt = initSpinnerView(act, tf.etxtl, fieldValue, pojoUI,
					mFieldViewMap, mPojoUImap);

			evt = eevt;

			// edviewLayout = el;
		} else if (sEditor.equals("DatePicker")) {

			EditText eevt = initDatePickerView(act, fieldValue, fieldType,
					pojoUI);
			eevt.setLayoutParams(tf.etxtl);
			eevt.setKeyListener(null);
			evt = eevt;

		} else if (sEditor.equals("project.ui.tuya.TuYaView")) {

			// View eevt = Class.forName(sEditor).newInstance();
			TuYaView eevt = initTuYaView(act, null, byte[].class, pojoUI);
			evt = eevt;
			// edviewLayout = tf.vtxtl;

		} else if (sEditor.equals("WebView")) {
			String url =fieldValue.toString();
			
					
			WebView eevt = initWebView(act, url);

			evt = eevt;
			// edviewLayout = tf.wtxtl;

		} else {
			MyLog.False(pojoUI.PojoEditor() + ":NotSupported");
			evt = null;
			return null;
		}
		if (pojoUI.System().length() > 0) {
			
			CommitClick tclick = null;
			if(BTCallBack.class.isAssignableFrom(act.getClass()) ){
				 tclick = new CommitClick((BTCallBack)act,act);
			}else 
				tclick = new CommitClick(null,act);
			evt.setClickable(true);
			evt.setTag(PojoUI.class.hashCode(), pojoUI);
			evt.setTag(String.class.hashCode(), fieldValue);
			evt.setOnClickListener(tclick);
			if (TextView.class.isAssignableFrom(evt.getClass())) {
				((TextView) evt).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				;
			}
		}
		evt.setTag("fieldname".hashCode(), fdname);
		if ( formObject.getEditType() != 1) {
			if (!sEditor.equals("TextView"))
				evt.setEnabled(false);
			if (TextView.class.isAssignableFrom(evt.getClass())) {
				((TextView) evt).setTextColor(act.getResources().getColor(
						R.color.text_read_only));
			}
		}
		if ( ProjectActivity.class.isAssignableFrom(act.getClass()))
			evt.setOnFocusChangeListener( (ProjectActivity)act);
		return evt;
	}

	private static TextView initLeftTextView(final Activity act,
			TableFormLayout tf1, PojoUI pojoUI) {
		final TextView vt = new TextView(act);
		TableFormLayout tf;
		if (tf1 == null)
			tf = new TableFormLayout();
		else
			tf = new TableFormLayout(tf1);
		vt.setLayoutParams(tf.txtl);
		String sUIName = pojoUI.PojoUIName();
		if (!pojoUI.CanBeNull()) {
			sUIName = sUIName + " *";
			vt.setTextColor(Color.RED);
		} else
			vt.setTextColor(Color.BLACK);
		vt.setText(sUIName);
		vt.setMinEms(4);
		vt.setMaxEms(8);

		vt.setSingleLine(false);
		vt.setGravity(Gravity.CENTER);
		vt.setTextSize(14);
		vt.setPadding(2, 2, 2, 2);
		return vt;
	}

	private static TuYaView initTuYaView(ProjectActivity act, Object fieldValue,
			Type fieldType, PojoUI pojoUI) {
		
		project.ui.tuya.TuYaView tuyav = new project.ui.tuya.TuYaView(act, null,act.msgHandler);
		if( fieldType.equals(Bitmap.class)){
			tuyav.originalBitmap = (Bitmap) fieldValue;
		}
		return tuyav;
	}

	private static View initTextView(Activity act, LayoutParams txtl,
			Object fieldValue, Type fieldType, PojoUI pojoUI) {


		TextView eevt = new TextView(act);

		eevt.setLayoutParams(txtl);
		if (fieldType.equals(Date.class))
			fieldValue = Func.dateToString((Date) fieldValue);
		eevt.setText(String.valueOf(fieldValue == null ? "" : fieldValue));

		eevt.setTextColor(act.getResources().getColor(R.color.text_read_only));
		eevt.setSingleLine(false);
		eevt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		eevt.setMinEms(4);
		eevt.setTextSize(14);
		eevt.setPadding(5, 2, 2, 2);

		return eevt;
	}

	private static View initEditTextView(Activity act,
			LayoutParams txtl, Object fieldValue, Type fieldType, PojoUI pojoUI) {

		EditText eevt = new EditText(act);

		eevt.setLayoutParams(txtl);
		eevt.setText(String.valueOf(fieldValue == null ? "" : fieldValue));
		eevt.setTextColor(Color.BLUE);
		eevt.setSingleLine(false);
		eevt.setMaxLines(5);
		eevt.setMinEms(6);
		if (fieldType.equals(Integer.class) || fieldType.equals(Float.class)
				|| fieldType.equals(Double.class)) {
			if (fieldType.equals(Integer.class))
				eevt.setInputType(InputType.TYPE_CLASS_NUMBER);
			if (fieldType.equals(Double.class) || fieldType.equals(Float.class))
				eevt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			eevt.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			eevt.setPadding(2, 2, 5, 2);
		} else {
			eevt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			eevt.setPadding(5, 2, 2, 2);
		}
		eevt.setTextSize(14);

		// eevt.setHorizontallyScrolling(true);

		// eevt.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		if (ProjectActivity.class.isAssignableFrom(act.getClass())){
			ProjectActivity pact = (ProjectActivity)act;
			if (pact.isWatchable())
			eevt.addTextChangedListener(new ProjectActivity.ThyTextWatcher(	eevt, pact));
		}
		return eevt;
	}
	@SuppressWarnings("unused")
	private static View initScannerView(final Activity act,
			LayoutParams txtl, Object fieldValue, Type fieldType, PojoUI pojoUI) {
		LayoutInflater inflater = LayoutInflater.from(act); 
		View scantextv = inflater.inflate(R.layout.edit_scan, null);
		EditText eevt = (EditText) scantextv.findViewById(R.id.scantext);
		Button bt = (Button)scantextv.findViewById(R.id.scan_bt);
		bt.setMaxHeight(20);
		bt.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						Intent _intent = new Intent(act, CaptureActivity.class);
						
						act.startActivityForResult(_intent,
								(int) CaptureActivity.serialVersionUID);
					}
				});
		scantextv.setLayoutParams(txtl);
		eevt.setText(String.valueOf(fieldValue == null ? "" : fieldValue));
		eevt.setTextColor(Color.BLUE);
		eevt.setSingleLine(false);
		eevt.setMaxLines(5);
		eevt.setMinEms(6);
		if (fieldType.equals(Integer.class) || fieldType.equals(Float.class)
				|| fieldType.equals(Double.class)) {
			if (fieldType.equals(Integer.class))
				eevt.setInputType(InputType.TYPE_CLASS_NUMBER);
			if (fieldType.equals(Double.class) || fieldType.equals(Float.class))
				eevt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			eevt.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			eevt.setPadding(2, 2, 5, 2);
		} else {
			eevt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			eevt.setPadding(5, 2, 2, 2);
		}
		eevt.setTextSize(16);

		// eevt.setHorizontallyScrolling(true);

		// eevt.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		if (ProjectActivity.class.isAssignableFrom(act.getClass())){
			ProjectActivity pact = (ProjectActivity)act;
			if (pact.isWatchable())
			eevt.addTextChangedListener(new ProjectActivity.ThyTextWatcher(	eevt, pact));
		}
		scantextv.setTag(eevt);
		return scantextv;
	}
	private static Spinner initSpinnerView(ProjectActivity act, LayoutParams ltxt,
			Object fieldValue, PojoUI pojoUI,
			HashMap<String, Bpojo.ViewHolder> mFieldViewMap,
			TreeMap<String, PojoUI> mPojoUImap) {
		Spinner evt = new Spinner(act);

		Spinner sevt = (Spinner) evt;
		Class<? extends Bpojo> cn = pojoUI.SpinnerClass();
		String[] items = {};

		items = Bpojo.getListItemValues(cn);
		ArrayAdapter<Object> ad = new ArrayAdapter<Object>(act,
				R.layout.spinner_item, items);
		evt.setLayoutParams(ltxt);

		sevt.setAdapter(ad);

		String subSpinner = pojoUI.SubSpinner();
		String subField = pojoUI.SubField();
		if (subSpinner != null && subSpinner.length() > 0) {
			sevt.setTag("subSpinner".hashCode(), items);

			sevt.setOnItemSelectedListener(new SpinnerListener(subSpinner,
					subField, cn, act, mFieldViewMap, mPojoUImap));
		}else 
			sevt.setOnItemSelectedListener(new SpinnerListener(null,
					null, null, act, mFieldViewMap, mPojoUImap));
		ValuePosition p = null;
		if (fieldValue != null) {
			p = Bpojo.findListItemPositon(cn, fieldValue, KEYVALUE.FIND_BY_KEY);
			if (p.findOut != null)
				sevt.setSelection(p.position);
			else
				sevt.setSelection(ListView.INVALID_POSITION);
		} else
			sevt.setSelection(ListView.INVALID_POSITION);

		return evt;

	}

	private static EditText initDatePickerView(final Activity act,
			Object fieldValue, Type fieldType, PojoUI pojoUI) {

		final EditText eevt = new EditText(act);

		try {
			eevt.setText(Func.dateToString((Date) fieldValue));
			eevt.setOnClickListener(new DataTimeFieldListener(eevt, act,
					fieldType));
			
		} catch (Exception e) {
	
			MyLog.False(e);
		}

		return eevt;
	}

	public static class SpinnerListener implements OnItemSelectedListener {
		// PojoUI spinner;
		String subSpinfieldname;
		String subBpojofieldname;
		Class<? extends Bpojo> pPojoClass;
		ProjectActivity act;
		HashMap<String, Bpojo.ViewHolder> mFieldViewMap = new HashMap<String, Bpojo.ViewHolder>();
		TreeMap<String, PojoUI> mPojoUImap = null;

		public SpinnerListener(String subSpinfieldname,
				String subBpojofieldname, Class<? extends Bpojo> cn,
				ProjectActivity act, HashMap<String, Bpojo.ViewHolder> mFieldViewMap,
				TreeMap<String, PojoUI> mPojoUImap) {
			super();
			this.subSpinfieldname = subSpinfieldname;
			this.subBpojofieldname = subBpojofieldname;
			this.pPojoClass = cn;
			this.act = act;
			this.mFieldViewMap = mFieldViewMap;
			this.mPojoUImap = mPojoUImap;
		}
		// TODO Auto-generated method stub
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if( subSpinfieldname == null ){
				act.onItemSelected(arg0,arg1,arg2,arg3);
				return;
			}
				
			try {
				String[] items = (String[]) arg0
						.getTag("subSpinner".hashCode());
				Object subspinnerfkeyvalue = Bpojo.findListItemPositon(
						pPojoClass, items[arg2], KEYVALUE.FIND_BY_VALUE).findOut;
				// 查找 subspinner field
				// String subSpinfieldname = spinner.SubSpinner();
				MyLog.Assert(mFieldViewMap.get(subSpinfieldname) != null, "");
				Spinner subSpinner = (Spinner) mFieldViewMap
						.get(subSpinfieldname).ev;
				PojoUI subFieldpojoUI = mPojoUImap.get(subSpinfieldname);

				Bpojo subPojo = (Bpojo) subFieldpojoUI.SpinnerClass()
						.newInstance();
				String[] subSpinValue = subPojo.getListItemValues(
						subBpojofieldname, subspinnerfkeyvalue);
				ArrayAdapter<Object> ad = new ArrayAdapter<Object>(act,
						R.layout.spinner_item, subSpinValue);
				subSpinner.setAdapter(ad);

			} catch (InstantiationException e) {
				
				MyLog.False(e);
			} catch (IllegalAccessException e) {
				
				MyLog.False(e);
			}

	

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {


		}
	}
/*	
	static private void createDateTimeDialog() {
		DateTimePickerDialog timePicker = new DateTimePickerDialog(this,
				new DateTimePickerDialog.ICustomDateTimeListener() {
					@Override
					public void onSet(Calendar calendarSelected,
							Date dateSelected, int year, String monthFullName,
							String monthShortName, int monthNumber, int date,
							String weekDayFullName, String weekDayShortName,
							int hour24, int hour12, int min, int sec,
							String AM_PM) {
						SimpleDateFormat format = new SimpleDateFormat(Func.YYYY_MM_DD_HH_MM_SS);
						time.setText(format.format(dateSelected));
					}

					@Override
					public void onCancel() {
						Log.d("datetimepickerdialog", "canceled");
					}
				});
		timePicker.set24HourFormat(true);
		timePicker.showDialog();
	}
	
	*/
	// private DatePickerDialog.OnDateSetListener listener = new
	static public class DatePickerDialogListener implements
			DatePickerDialog.OnDateSetListener { //

		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			cal.clear();

			cal.set(Calendar.YEAR, arg1);

			cal.set(Calendar.MONTH, arg2);

			cal.set(Calendar.DAY_OF_MONTH, arg3);
			java.sql.Date sqldate = new java.sql.Date(cal.getTime().getTime()) ;
			
		
			tvDate.setText(Func.dateToString(sqldate));

		}

		public DatePickerDialogListener(TextView tvDate) {
			super();

			this.tvDate = tvDate;
		}

		private Calendar cal = Calendar.getInstance();
		private TextView tvDate;

		// 当 DatePickerDialog 关闭，更新日期显示

		private void updateDate() {

			tvDate.setText(Func.dateToString(cal.getTime()));

		}
	};

	private static final class DataTimeFieldListener implements
			View.OnClickListener {
		private final EditText eevt;
		private final Activity act;
		private Object fieldValue;
		Date ca;
		private Type fieldType;
		Calendar cal = Calendar.getInstance();

		private DataTimeFieldListener(EditText eevt, Activity act,
				Type fieldType) {
			this.eevt = eevt;
			this.act = act;
			// this.dv = dv;
			this.fieldType = fieldType;

		}

		public void onClick(View v) {
			TextView tv = (TextView) v;
			try {
				fieldValue = tv.getText();
				if (fieldValue == null || fieldValue.toString() == null)
					ca = new Date();
				else if (fieldValue.toString().length() == 0)
					ca = new Date();
				else
					ca = Func.toDate(fieldValue.toString());
			} catch (ParseException e) {
			
				MyLog.False(e);
			}

			cal.setTime(ca);
			if (fieldType.equals(java.sql.Date.class)) {

				new DatePickerDialog(act, new DatePickerDialogListener(eevt),
						cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH)).show();

			} else if (fieldType.equals(java.util.Date.class)) {

				DateTimePickerDialog datetimePicker = new DateTimePickerDialog(act,
						new DateTimePickerDialog.ICustomDateTimeListener() {
							@Override
							public void onSet(Calendar calendarSelected,
									Date dateSelected, int year,
									String monthFullName,
									String monthShortName, int monthNumber,
									int date, String weekDayFullName,
									String weekDayShortName, int hour24,
									int hour12, int min, int sec, String AM_PM) {

								Date d = new Date(dateSelected.getTime() / 60000 * 60000);
								eevt.setText(Func.dateToString(d));
							}

							@Override
							public void onCancel() {
								Log.d("datetimepickerdialog", "canceled");
								
							}

							@Override
							public void onClear() {
								
								eevt.setText("");
								
							}
						}, cal);
				datetimePicker.set24HourFormat(true);
				datetimePicker.showDialog();
			} else if (fieldType.equals(java.sql.Time.class)) {

				Dialog dialog = null;

				TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {

						cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
						cal.set(Calendar.MINUTE, minute);
						cal.set(Calendar.SECOND, 0);

						// SimpleDateFormat df = new SimpleDateFormat(
						// Func.HH_MM, Locale.US);
						java.sql.Time t = new Time(cal.getTime().getTime());
						eevt.setText(Func.timeToString(t));

					}

				};

				dialog = new TimePickerDialog(act, timeListener,
						cal.get(Calendar.HOUR_OF_DAY),
						cal.get(Calendar.DAY_OF_MONTH), true);
				dialog.show();
			}
		}
	}
}

/**
 * 
 */
package project.pojo;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.View;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tgb.lk.ahibernate.annotation.Column;

import project.annotation.map.PojoMap;
import project.annotation.ui.PojoDescription;
import project.annotation.ui.PojoUI;
import project.pojo.Bpojo.ViewHolder;
import project.ui.tuya.TuYaView;
import project.util.Func;
import project.util.MyLog;

/**
 * @author thomasy
 * 
 */

public class Bpojo implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = Bpojo.class.hashCode();

	/**
	 * @PojoUI(PojoUIName="机器地址", PojoUIOrder = 0, PojoEditor=1)
	 * 
	 */

	// protected String sid = "";
	private boolean isFromDB = false;

	/**
	 * @return 是否从手机Db中读取
	 */
	public boolean isFromDB() {
		return isFromDB;
	}

	/**
	 * @param isFromDB
	 *            the isFromDB to set
	 */
	public Object getID(){
		return "";
	}
	public void setFromDB(boolean isFromDB) {
		this.isFromDB = isFromDB;
	}

	public Date getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(Date fetchTime) {
		this.fetchTime = fetchTime;
	}

	public Date getLocalDBTime() {
		return localDBTime;
	}

	public void setLocalDBTime(Date localDBTime) {
		this.localDBTime = localDBTime;
	}

	final private ArrayList<String> nullFields = new ArrayList<String>();
	@Column(name = "editType")
	private int editType = 1;
	// transient private Bpojo[] ItemList;
	@Column(name = "fetchTime")
	volatile protected Date fetchTime;
	@Column(name = "localDBTime")
	volatile protected Date localDBTime;

	public Bpojo() {
		super();
		initBpojo();
		// TODO Auto-generated constructor stub
	}

	private void initBpojo() {
		// TODO Auto-generated method stub
		
	}

	// private HashMap<String,String> lfds;
	public JSONObject checkJsonField(org.json.JSONObject js) {
		return checkJsonField(getClass(), js);
	}
/**
 * 检验 JSON格式， 是否与相应的class匹配， 字段有否差异
 * @param c：相关对象的类型
 * @param jsobj：承载对象
 */
	public static void verifyJson(Class<?> c, Object jsobj) {
		if (jsobj.getClass().equals(JSONObject.class)) {
			JSONObject re = Bpojo.checkJsonField(c, (JSONObject) (jsobj));
			if (re.names() != null && re.names().length() > 0){
				MyLog.Loge("verifyJson: " + c.getName() + ":");
				MyLog.Loge(re);
			}

		} else if (jsobj.getClass().equals(JSONArray.class)) {
			JSONArray re = Bpojo.checkJsonArrayField(c, (JSONArray) jsobj);
			if (re.length() > 0){
				MyLog.Loge("verifyJson: " + c.getName() + ":" );
				MyLog.Loge(re);
			}

		} else
			return;
		// MyLog.False("Not a Json!");
	}

	public static JSONArray checkJsonArrayField(Class<?> t,
			org.json.JSONArray js) {
		JSONArray ja = new JSONArray();

		for (int i = 0; i < js.length(); i++) {
			JSONObject jret = null;
			try {
				JSONObject jo = js.getJSONObject(i);
				jret = checkJsonField(t.getComponentType(), jo);
				if (jret.length() > 0)
					ja.put(jret);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ja;
	}

	
	/**
	 * 对class里的每个field进行验证，如果没有在class里找到对应，就logout一个错误
	 * @param t: 验证的class
	 * @param js： 验证的 js
	 * @return 
	 */
	public static JSONObject checkJsonField(Class<?> t, org.json.JSONObject js) {

		JSONObject retj = new JSONObject();
		String name = "";
		if (js == null)
			return retj;
		org.json.JSONArray ja = js.names();
		if (ja == null) {
			return retj;
		}

		for (int i = 0; i < ja.length(); i++) {

			try {
				Object jo = ja.get(i);
				name = jo.toString();
				// MyLog.Log(jo.toString());
				Field fd = t.getDeclaredField(name);
				if (Modifier.isPrivate(fd.getModifiers()))
					throw new NoSuchFieldException("Private");
				Object value = js.get(name);
				if (value == null)
					continue;
				if (value.getClass().equals(JSONObject.class)) {
					Class<?> fdc = fd.getType();
					JSONObject r = checkJsonField(fdc, (JSONObject) value);
					if (r.length() > 0)
						retj.put(name, r);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {

				// TODO Auto-generated catch block
				try {
					if (e.getMessage().equals("Private"))
						retj.put(name, "private define");
					else
						retj.put(name, "field lost");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					MyLog.False(e1);
				}
			}
		}

		return retj;
	}

	public String getClassAnnotation() {

		String ret = "";
		Class<? extends Bpojo> pojoclass = this.getClass();
		boolean flag = pojoclass
				.isAnnotationPresent((Class<? extends Annotation>) PojoDescription.class);
		if (flag) {
			PojoDescription des = pojoclass
					.getAnnotation(PojoDescription.class);
			ret = des.value();
		}
		return ret;
	}

	class PojoUIComparator implements Comparator<Object> {

		private Map<String, PojoUI> me;

		public Map<String, PojoUI> getMe() {
			return me;
		}

		public void setMe(Map<String, PojoUI> me) {
			this.me = me;
		}

		public PojoUIComparator() {
			super();

			// TODO Auto-generated constructor stub
		}

		@Override
		public int compare(Object o1, Object o2) {
			PojoUI obj1 = (PojoUI) me.get((String) o1);
			PojoUI obj2 = (PojoUI) me.get((String) o2);
			int i1 = obj1.PojoUIOrder();
			int i2 = obj2.PojoUIOrder();
			return i1 - i2;
		}

	}

	@SuppressWarnings("unchecked")
	public <T> TreeMap<String, T> getFieldsAnnotation(
			Class<? extends Annotation> AnnoCls,
			@SuppressWarnings("rawtypes") Comparator c) {
		/*
		 * Annotation o=null; try { o = AnnoCls.newInstance();
		 * 
		 * } catch (InstantiationException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (IllegalAccessException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } MyLog.Log(o);
		 */
		TreeMap<String, T> mp = new TreeMap<String, T>();

		Class<? extends Bpojo> pojoclass = this.getClass();
		// Field fd=null;
		Field fds[] = pojoclass.getDeclaredFields();
		Method md = null;
		Object oAnno = null;
		for (Field fd : fds) {
			if (!fd.isAnnotationPresent(AnnoCls))
				continue;
			T pojoAnno = (T) fd.getAnnotation(AnnoCls);
			if (pojoAnno != null) {

				oAnno = pojoAnno;
				try {
					md = pojoAnno.getClass().getMethod("Comaprator");
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}

				mp.put(fd.getName(), pojoAnno);
			}
		}
		if (c == null && md != null) {
			try {

				@SuppressWarnings({ "rawtypes" })
				Class<? extends Comparator> ccls = (Class<? extends Comparator>) md
						.invoke(oAnno);
				@SuppressWarnings("rawtypes")
				Constructor<? extends Comparator> cc = ccls
						.getConstructor(Map.class);
				c = cc.newInstance(mp);
				// c = ccls.newInstance();
			} catch (Exception e) {
				MyLog.False(e);
			}
		}
		TreeMap<String, T> mp2 = null;
		if (c != null)
			mp2 = new TreeMap<String, T>(c);
		else
			mp2 = new TreeMap<String, T>();
		mp2.putAll(mp);
		// Collections.sort(mp);
		return mp2;

	}

	public TreeMap<String, PojoUI> getFieldsAnnotation() {
		TreeMap<String, PojoUI> mp = new TreeMap<String, PojoUI>();
		PojoUIComparator c = new PojoUIComparator();
		TreeMap<String, PojoUI> mp2 = new TreeMap<String, PojoUI>(c);
		c.setMe(mp);
		// (PojoUIComparator) new PojoUIComparator()
		// PojoUIComparator c = (PojoUIComparator) mp2.comparator();

		Class<? extends Bpojo> pojoclass = this.getClass();
		// Field fd=null;
		Field fds[] = pojoclass.getDeclaredFields();
		for (Field fd : fds) {

			PojoUI pojoUI = fd.getAnnotation(PojoUI.class);

			if (pojoUI != null)
				mp.put(fd.getName(), pojoUI);
		}
		mp2.putAll(mp);
		// Collections.sort(mp);
		return mp2;

	}

	public PojoUI getFieldAnnotation(String field) {
		PojoUI ret = null;

		// final String CLASS_NAME = "annotation.JavaEyer";
		// Class test = Class.forName(CLASS_NAME);
		Class<? extends Bpojo> pojoclass = this.getClass();
		Field fd = null;
		try {
			fd = pojoclass.getDeclaredField(field);
			PojoUI pojoUI = fd.getAnnotation(PojoUI.class);
			ret = pojoUI;
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			MyLog.Assert(false, e);
		}

		return ret;

	}

	public String getMethodAnnotation(String method) {
		String ret = "";

		Class<? extends Bpojo> pojoclass = this.getClass();
		Method md = null;
		try {
			md = pojoclass.getDeclaredMethod(method);
			PojoUI pojoUI = md.getAnnotation(PojoUI.class);
			ret = pojoUI.PojoUIName();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}

		return ret;

	}
/*
 * @return 返回相关的 fn 的值， 
 * @throws 如果不存在该field，抛出  NoSuchFieldException
 */
	public Object getFieldValue(String fn) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{

		
			// MyLog.Log(getClass().getName());
			Field fs = this.getClass().getDeclaredField(fn);
			fs.setAccessible(true);
			/*
			 * thy-TODO -sure? if
			 * (fs.getType().isAssignableFrom(Date.class)) retobj[i++] =
			 * Func.dateToString((Date) fs.get(this)); else
			 */
			Object ret = fs.get(this);
			return ret;
		
	}
	
	public boolean compare(String value) {
		// TODO Auto-generated method stub
		return search(value);
	}
	public  boolean search( String value){
		if ( value==null || value.length()==0)
			return true;
		Object ol[]=getFieldsValue();
		for (Object v:ol)
		{
			if ( v!=null && v.getClass().equals(String.class)){
				int ret = v.toString().toLowerCase(Locale.US).indexOf(value.toLowerCase(Locale.US));
				if ( ret != -1 )
					return true;
			}
		}
		return false;
		 
	}
	
	
	public Object[] getFieldsValue(){
		
		Field fds[] = this.getClass().getDeclaredFields();
		ArrayList<String> sl =  new ArrayList<String>();
		for( Field fd:fds)
		{
			sl.add(fd.getName());
		}
		
		return getFieldsValue( sl.toArray(new String[0]));
		
	}
	/*
	 * @return
	 * 返回fields列表对应的 value 列表， 如果field不存在， 则赋值对应的exception
	 */
	
	public Object[] getFieldsValue(String... fns) {
		Object[] retobj = new Object[fns.length];
		// MyLog.Log(this.getClass().getName());
		int i = 0;
		
		for (String fn : fns) {
			if (fn == null)
				continue;
			try {
				retobj[i++] = getFieldValue(fn);
				 
			} catch (NoSuchFieldException e) {
				retobj[i++]=e; 
				// TODO Auto-generated catch block
				MyLog.Loge(e);
				continue;
			} catch (IllegalArgumentException e) {
				retobj[i++]=e; 
				// TODO Auto-generated catch block
				MyLog.Loge(e);
				continue;
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				retobj[i++]=e; 
				// TODO Auto-generated catch block
				MyLog.Loge(e);
				continue;
			}

		}
		return retobj;
	}

	/*
	 * public class AaSerializer implements JsonSerializer<ArrayList>{ public
	 * JsonElement toJson(ArrayList id, Type typeOfId, JsonSerializationContext
	 * context) {
	 * 
	 * return new (id.toString()); }
	 * 
	 * @Override public JsonElement serialize(ArrayList arg0, Type arg1,
	 * JsonSerializationContext arg2) { // TODO Auto-generated method stub
	 * return null; } }
	 */
	@Override
	public String toString() {
		Gson gs = Func.createGson();

		return gs.toJson(this);

	}

	public void copyValue(Object source) {
		if (source == null)
			return;
		HashMap<String, String> mfds = new HashMap<String, String>();
		Field[] lfds = source.getClass().getDeclaredFields();
		for (Field fd1 : lfds) {
			mfds.put(fd1.getName(), "");

		}

		setObjectValue(this.getClass(), 1, source, mfds);
		if (mfds.size() > 0)
			MyLog.Log("copyValue: " + mfds);
	}

	public Object checkValue() {

		return setObjectValue(this.getClass(), 0, null, null);
		// TODO Auto-generated method stub

	}

	public Object setObjectValue() {
		return setObjectValue(this.getClass(), 1, null, null);
	}

	public Object setObjectValue(Class<?> c, int setvalue, Object source,
			HashMap<String, String> lfds) {

		Set<String> ss = new HashSet<String>();
		Class<?> supc = c.getSuperclass();
		Method md;
		Object ret = null;
		if (c != Bpojo.class)
			try {// thy-todo call super.setObjectValue?
				md = supc.getMethod("setObjectValue", supc.getClass(),
						int.class, Object.class, HashMap.class);

				md.invoke(this, supc, setvalue, source, lfds);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Field[] fs = c.getDeclaredFields();
		// Field[] sfds=null;

		if (setvalue == 0)
			nullFields.clear();
		for (Field fd : fs) {
			if (c == Bpojo.class) {
				int ms = fd.getModifiers();
				if ((ms & Modifier.PRIVATE) != 0)
					continue;
			}
			ret = null;
			// fd.set(this, value)
			Class<?> cl = fd.getType();
			if (source != null) {
				// Field[] sfds;
				MyLog.Assert(lfds != null, "");

				try {
					Field sfd = source.getClass()
							.getDeclaredField(fd.getName());
					sfd.setAccessible(true);
					ret = sfd.get(source);
					lfds.remove(sfd.getName());

				} catch (NoSuchFieldException e) {
					continue;
					// TODO Auto-generated catch block

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					MyLog.False(e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					MyLog.False(e);
				}
			} else if (cl.isAssignableFrom(String.class))
				ret = "" + fd.getName().charAt(0) + "-v";
			else if (cl.isAssignableFrom(Integer.class) || cl == int.class)
				ret = 1;
			else if (cl.isAssignableFrom(Set.class))
				ret = ss;
			else if (cl.isAssignableFrom(Boolean.class) || cl == boolean.class)
				ret = true;
			else if (cl.isAssignableFrom(Long.class) || cl == long.class)
				ret = 100001;
			else if (cl.isAssignableFrom(Float.class) || cl == float.class)
				ret = 1.1;
			else if (cl.isAssignableFrom(Double.class) || cl == double.class)
				ret = 1.000001;
			else if (Date.class.equals(cl)) {
				ret = new Date();

			} else if (java.sql.Date.class.equals(cl)) {

				ret = new java.sql.Date(new Date().getTime());

			} else if (java.sql.Time.class.equals(cl)) {

				ret = new java.sql.Time(new Date().getTime());

			} else if (Bpojo.class.isAssignableFrom(cl))
				try {
					fd.setAccessible(true);
					Bpojo pojoFieldObject = ((Bpojo) fd.get(this));
					if (pojoFieldObject == null) {
						pojoFieldObject = (Bpojo) cl.newInstance();
						// fd.set(this, pojoFieldObject);
					}
					pojoFieldObject.setObjectValue();
					ret = pojoFieldObject;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					MyLog.False(e1);
				}
			else {
				MyLog.Assert(false, cl.getName() + " :"
						+ this.getClass().getName() + "." + fd.getName()
						+ ": 不被支持");
				continue;
			}
			try {

				if (!(Modifier.isFinal(fd.getModifiers()) || Modifier
						.isStatic(fd.getModifiers()))) {
					fd.setAccessible(true);
					if (setvalue == 1) {
						fd.set(this, ret);
						if (ret == null)
							nullFields.add(fd.getName());
					} else if (fd.get(this) == null)
						nullFields.add(fd.getName());

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				MyLog.False(e);
				// e.printStackTrace();

			}
		}

		if (setvalue == 0)
			return printNullFields();
		return this;

	}

	public String printNullFields() {
		if (this.nullFields.size() > 0)
			return this.nullFields.toString();
		return "";
	}

	public static Bpojo[] getPojoItemList(Bpojo bpojo)
			throws InvocationTargetException, NoSuchMethodException {
		// Bpojo[] ret = {};
		Class<? extends Bpojo> c = bpojo.getClass();
		Method getPojoItemList = null;
		Field itemListFd = null;
		Bpojo[] itemList = null;
		// if (Bpojo.class.isAssignableFrom(c))
		// return ret;
		try {
			itemListFd = c.getDeclaredField("ItemList");
			itemListFd.setAccessible(true);
			itemList = (Bpojo[]) itemListFd.get(c);
			if (itemList == null || itemList.length == 0) {
				getPojoItemList = c.getDeclaredMethod("getPojoItemList");
				itemList = (Bpojo[]) getPojoItemList.invoke(bpojo);//
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}

		return itemList;

	}

	public Bpojo[] getPojoItemList() throws Exception {

		Bpojo[] ret = {};
		ret = getPojoItemList(this);
		return ret;

	}

	public static String[] getListItemValues(Class<? extends Bpojo> c) {

		return getListItemValues(c, null, null);

	}

	public static String[] getListItemValues(Class<? extends Bpojo> c,
			String subField, Object value) {

		Bpojo b;
		String ret[] = {};
		try {
			b = c.newInstance();
			return b.getListItemValues(subField, value);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}
		return ret;

	}

	public String[] getListItemValues() {

		return getListItemValues(null, null);

	}

	public Field getPojoMapKeyField() {

		Field fdkey = null;

		TreeMap<String, PojoMap> fmap = getFieldsAnnotation(PojoMap.class, null);
		Set<Entry<String, PojoMap>> es = fmap.entrySet();
		Class<? extends Bpojo> pojoclass = this.getClass();
		String fdn = null;
		try {
			for (Entry<String, PojoMap> et : es) {

				fdn = et.getKey();
				if (et.getValue().key() == 1) {

					// Field fd=null;
					fdkey = pojoclass.getDeclaredField(fdn);
					break;

				}
			}
			MyLog.Assert(fdkey != null, getClass().getName());

			return fdkey;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}
		MyLog.False("");
		return null;

	}

	public Field getPojoMapValueField() {

		Field fdvalue = null;
		TreeMap<String, PojoMap> fmap = getFieldsAnnotation(PojoMap.class, null);
		Set<Entry<String, PojoMap>> es = fmap.entrySet();
		try {
			for (Entry<String, PojoMap> et : es) {
				Class<? extends Bpojo> pojoclass = this.getClass();
				String fdn = et.getKey();
				if (et.getValue().key() == 0) {

					// Field fd=null;
					fdvalue = pojoclass.getDeclaredField(fdn);
					break;

				}
			}

			MyLog.Assert(fdvalue != null, getClass().getName());
			return fdvalue;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}
		MyLog.False("");
		return null;

	}

	static public enum KEYVALUE {
		FIND_BY_KEY, FIND_BY_VALUE
	};

	static public class ValuePosition {
		public Integer position = null;
		public Object findOut = null;
	}

	public static class ViewHolder {
		public TextView tv;
		public View ev;
		public PojoUI pojoUI;
	}

	/*
	 * static ValuePotion findListItemPositon(Class<? extends Bpojo> pojo,Object
	 * value,KEYVALUE findByKey ){
	 * 
	 * return pojo
	 * 
	 * }
	 */
	static public ValuePosition findListItemPositon(
			Class<? extends Bpojo> spinnerClassName, Object sItem,
			KEYVALUE findByValue) {

		Bpojo spinbpojo;
		ValuePosition ret = new ValuePosition();
		try {
			spinbpojo = spinnerClassName.newInstance();
			ret = spinbpojo.findListItemPositon(sItem, findByValue);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}

		// TODO Auto-generated method stub
		return ret;
	}

	public ValuePosition findListItemPositon(Object v, KEYVALUE findByKey) {

		ValuePosition ret = new ValuePosition();
		Field fdkey = null;
		Field fdvalue = null;
		try {
			Bpojo[] ItemList = getPojoItemList(this);
			fdkey = this.getPojoMapKeyField();
			fdvalue = this.getPojoMapValueField();
			if (fdkey == null || fdvalue == null || v == null)
				return ret;
			fdkey.setAccessible(true);
			fdvalue.setAccessible(true);
			int pos = 0;
			Object comV = null;
			for (Bpojo m : ItemList) {
				if (findByKey == KEYVALUE.FIND_BY_KEY) {
					comV = fdkey.get(m);

				} else
					comV = fdvalue.get(m);

				if (comV.equals(v)) {
					if (findByKey == KEYVALUE.FIND_BY_KEY)
						ret.findOut = fdvalue.get(m);
					else
						ret.findOut = fdkey.get(m);
					ret.position = pos;
					return ret;
				}
				pos++;
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (Exception e) {
			// thy-TODO Auto-generated catch block
			e.printStackTrace();
			return ret;
		}

		MyLog.Loge(this.getClass().getName() + ":List code Not Found for:" + v);
		return ret;
	}

	public String[] getListItemValues(String subField, Object value) {
		ArrayList<String> ret = new ArrayList<String>();
		try {
			Bpojo[] ItemList = getPojoItemList(this);
			if (ItemList == null)
				;
			else {
				for (Bpojo m : ItemList) {

					if (subField != null) {

						if (m.getFieldValue(subField).equals(value))
							ret.add(m.getPojoMapValueField().get(m).toString());

					} else

						ret.add(m.getPojoMapValueField().get(m).toString());
				}
			}
		} catch (Exception e) {
			// thy-todo TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return (String[]) ret.toArray(new String[0]);
		// TODO Auto-generated method stub

	}

	public Object getViewValue(Object view, String fn, PojoUI pui) {

		Object ret = null;
		Object v = null;
		Field fd = null;
		Class<?> retType = null;
		Object fieldValue = null;
		if (view.getClass().equals(WebView.class)) {
			ret = Void.class;
		}
		if (view.getClass().equals(TextView.class)) {
			TextView tv = (TextView) view;
			ret = tv.getText();
		}
		if (view.getClass().equals(EditText.class)) {
			EditText tv = (EditText) view;
			ret = tv.getText();
		}
		if (view.getClass().equals(Spinner.class)) {
			Spinner tv = (Spinner) view;
			Object sItem = tv.getSelectedItem();
			if (sItem != null) {
				// PojoUI pjo = (PojoUI) getFieldAnnotation(PojoUI.class, null);

				ret = findListItemPositon(pui.SpinnerClass(), sItem,
						KEYVALUE.FIND_BY_VALUE).findOut;
				MyLog.Assert(ret != null, getClass() + ":未查到相应的key:" + sItem);
			} else
				ret = null;
		}
		if (view.getClass().equals(DatePicker.class)) {
			TextView tv = (TextView) view;
			ret = tv.getText();
			if (ret == null || ret.toString().length() == 0) {
				ret = null;
			}
		}
		if (view.getClass().equals(TuYaView.class)) {
			TuYaView tv = (TuYaView) view;
			ret = tv.saveImage();
		}
		if (ret == null || ret.toString().length() == 0)
			return null;
		if (fn == null || fn.length() == 0)
			return ret;
		try {
			fd = this.getClass().getDeclaredField(fn);
			retType = fd.getType();
			if (!retType.equals(Bitmap.class))
				fieldValue = ret.toString();
			else
				fieldValue = ret;
			if (retType.equals(Integer.class))
				v = Integer.parseInt(fieldValue.toString());
			else if (retType.equals(Long.class))
				v = Long.parseLong(fieldValue.toString());
			else if (retType.equals(Float.class))
				v = Float.parseFloat(fieldValue.toString());
			else if (retType.equals(Double.class))
				v = Double.parseDouble(fieldValue.toString());
			else if (retType.equals(Boolean.class))
				v = Boolean.valueOf(fieldValue.toString());
			else if (retType.equals(java.util.Date.class))

				v = Func.toDate(fieldValue.toString());

			else if (retType.equals(java.sql.Date.class))
				v = Func.toSQLDate(fieldValue.toString());
			else if (retType.equals(java.sql.Time.class))
				v = Func.toSQLTime(fieldValue.toString());
			else if (retType.equals(String.class))
				v = fieldValue;
			else if (retType.equals(Bitmap.class))
				v = fieldValue;
			else {
				MyLog.False("类型不被支持:" + retType);
				v = fieldValue;
			}
			MyLog.Log(v);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			MyLog.False(e);
		}
		MyLog.Assert(v != null, "");
		return v;
	}

	public HashMap<String, String> refreshData(
			HashMap<String, Bpojo.ViewHolder> viewMap, HashMap<String, String> retMap) {
		Field fd = null;
		if (retMap == null)
			retMap = new HashMap<String, String>();
		MyLog.Assert(viewMap!=null,"viewMap 不能为NULL");
		Set<String> ks = viewMap.keySet();
		for (String fn : ks) {
			Bpojo.ViewHolder os = viewMap.get(fn);
			Object tv = os.ev;
			PojoUI pui = (PojoUI) os.pojoUI;
			Object fieldValue = null;
			try {
				fieldValue = getViewValue(tv, fn, pui);

				if (fieldValue == null || fieldValue.toString().length() == 0) {
					;
					if (!pui.CanBeNull())
						retMap.put(pui.PojoUIName(), "不能为空");

				} else if (fieldValue.equals(Void.class))
					continue;
				else {
					fd = getClass().getDeclaredField(fn);
					fd.setAccessible(true);
					fd.set(this, fieldValue);

				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				MyLog.False(e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				MyLog.False(e);
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				MyLog.False(e);
			}
		}
		// TODO Auto-generated method stub
		return retMap;
	}
/**
 * @return 1: 可编辑
 *  其他：不可编辑
 */
	public int getEditType() {
		return editType;
	}

	public void setEditType(int editType) {
		this.editType = editType;
	}

	public SpannableString getDisplayStr(String name) {
		
		SpannableString ss = new SpannableString("");  
		if ( name == null || name.length()==0)
			return ss;
		Object fv;
		
		try {
			fv = getFieldValue(name);
			if( fv!= null )
				return new SpannableString(fv.toString());
			else return ss;
		} catch (Exception e ) {
			//MyLog.Loge(e);
			return ss;
		}
		
		// TODO Auto-generated method stub
		
	}

}

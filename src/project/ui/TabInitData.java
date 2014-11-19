package project.ui;

import java.io.Serializable;

import android.app.Activity;
import android.widget.TextView;

public class TabInitData implements Serializable {
	/**
	 * 
	 */
	public static final long serialVersionUID = TabInitData.class
			.hashCode();
	// @TAB显示的文字
	int NanmeRID;
	int tabid;
	TextView tv;
	//public ResolveOrderTabHostActivity rootAct ;
	public Activity rootAct ;
	//TODO thy change rootact to projecttabhost
	public TabInitData( Activity in) {
		rootAct = in;
	};

	public TabInitData(int nanmeRID, Activity cActivity, int sDrawRID,
			int nDrawRID) {
		super();
		NanmeRID = nanmeRID;
		this.tabActivity = cActivity;
		this.sDrawRID = sDrawRID;
		this.nDrawRID = nDrawRID;
	}

	public TabInitData() {
		// TODO Auto-generated constructor stub
	}

	public Activity tabActivity;
	// @选中时的图片
	int sDrawRID;
	// @没选中的图片
	int nDrawRID;

}
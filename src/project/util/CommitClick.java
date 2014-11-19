package project.util;

import android.app.Activity;
import android.view.View;

import com.thybase.R;

import project.annotation.ui.PojoUI;
import project.ui.BTCallBack;
import project.ui.ProjectActivity;
import android.widget.Button;

public class CommitClick implements Button.OnClickListener {
	public static final int DURATION = 15 * 1000;
	private final BTCallBack callBack;
	private final Activity rootAct;
	

	public CommitClick(BTCallBack MyAct, Activity _rootAct) {
		this.callBack = MyAct;

		rootAct = _rootAct;
	}
	
	public CommitClick(ProjectActivity MyAct) {
		
		this( MyAct,  MyAct);
	}/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//super.onClick(v);
		int id = v.getId();
		
		try {
			if (callBack!= null && id == R.id.BT_OK) {

				callBack.OnOK(v);

			} else if (callBack!= null && id == R.id.BT_CANCEL) {
				callBack.OnCancel(v);

			}else {
				PojoUI pj = (PojoUI) v.getTag( PojoUI.class.hashCode());
				if (pj !=null ){
					
					if (pj.System().equals("call")){
					    Func.makeCall(rootAct,v.getTag(String.class.hashCode()).toString());
					}
				}
			}

		} catch (Exception e) {
			
			MyLog.False(e);
			// TODO Auto-generated catch block
			// PriceUtil.catchException(MyAct, e);
		} finally {

		}

	}
}
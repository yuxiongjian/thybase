package project.ui;

import android.view.View;
import android.widget.AdapterView;

public interface BTCallBack {

	void OnOK(View v);

	void OnCancel(View v);
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) ;

}

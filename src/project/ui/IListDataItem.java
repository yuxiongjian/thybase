package project.ui;

import android.text.SpannableString;

public interface IListDataItem {

	public String getName();

	

	public String getBref();

	public String getTime();



	public String getSortKey();



	public Object getID();



	public CharSequence getAddress();

	public String getTitle();

	public boolean isUnRead();


	public boolean isReverse();
	public String getBarcode();
	public boolean compare(String value);

    //public String getStatusStr();
	public CharSequence getType();
	public SpannableString  getDisplayStr(String name);

}

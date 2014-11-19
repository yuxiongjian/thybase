package project.util.sqldb;

import project.config.Config;
import project.util.sqldb.dao.Student;
import project.util.sqldb.dao.Teacher;


import com.tgb.lk.ahibernate.util.MyDBHelper;









import android.content.Context;

public class DBHelper extends MyDBHelper {
	private static final String DBNAME = Config.DBNAME;
	private static final int DBVERSION = Config.DBVERSION;
	private static final Class<?>[] clazz = Config.DBCLASS; 
	public DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}

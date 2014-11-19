package project.util.sqldb.dao.impl;

import project.util.sqldb.DBHelper;
import project.util.sqldb.dao.Teacher;






import android.content.Context;

public class TeacherDaoImpl extends BaseDaoImpl<Teacher> {
	public TeacherDaoImpl(Context context) {
		super(new DBHelper(context),Teacher.class);
	}
}

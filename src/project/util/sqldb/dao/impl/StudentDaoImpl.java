package project.util.sqldb.dao.impl;

import project.util.sqldb.DBHelper;
import project.util.sqldb.dao.Student;






import android.content.Context;


public class StudentDaoImpl extends BaseDaoImpl<Student> {
	public StudentDaoImpl(Context context) {
		super(new DBHelper(context),Student.class);
	}
}

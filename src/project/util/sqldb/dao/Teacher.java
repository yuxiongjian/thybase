package project.util.sqldb.dao;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;

//�Զ����ɵĽ������:
//crate table [t_teacher]: CREATE TABLE t_teacher (id INTEGER primary key autoincrement, title TEXT, name TEXT(20), age INTEGER )

@Table(name = "t_teacher")
public class Teacher extends Person {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "title")
	private String title;// ְ��

	//@Column(name = "student")
	//private Student s;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	@Column(name = "level")
	protected  java.lang.Integer level; /** level  是否修复 **/
	
	@Column(name = "ismachineok" ,type="TEXT")
	public  java.lang.Boolean ismachineok; /** ismachineok  是否修复 **/
	
	@Column(name = "ismachine" )
	public  java.lang.Boolean ismachine; /** ismachineok  是否修复 **/
	
	@Column(name = "reservetime", type = "TEXT")//null 变为 0
	public  java.util.Date reservetime; /** reservetime  下次预约时间 **/
	
	@Column(name = "reserve", type = "LONG")
	volatile public  java.util.Date reserve; /** reservetime  下次预约时间 **/
	
}

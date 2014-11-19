package project.util.sqldb.dao;

import project.pojo.Bpojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;

//�˴�û�м�Table����,����������Ļ���,��������@Columnע����ֶ���������ͬ���ᱻ����������.
public class Person extends Bpojo {
	@Id
	@Column(name = "id")
	private int id; // ����,int����,���ݿ⽨��ʱ���ֶλ���Ϊ������

	@Column(name = "name", length = 20)
	private String name; // ���ֳ���һ�㲻�ᳬ��20���ַ���,length=20�����ֶεĳ�����20

	@Column(name = "age", type = "INTEGER")
	private Integer age; // ����һ������ֵ,��type = "INTEGER"�淶һ�°�.

	// //��������ʼʱû�д�����,���򿪷��в��뵽������,ȥ������ע�����԰�,���ݿ���ɾ�Ĳ鲻���޸��κδ���Ŷ.
	// @Column(name = "sex")
	// private String sex;

	// ��Щ�ֶ������ܲ�ϣ�����浽���ݿ���,����@Columnע�;Ͳ���ӳ�䵽���ݿ�.
	private String noSaveFild;
	public String toString() {
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss");

		Gson gs = new GsonBuilder().create();
		return gs.toJson(this);

	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getNoSaveFild() {
		return noSaveFild;
	}

	public void setNoSaveFild(String noSaveFild) {
		this.noSaveFild = noSaveFild;
	}

	
}

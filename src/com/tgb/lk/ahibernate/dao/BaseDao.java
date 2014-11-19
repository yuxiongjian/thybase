package com.tgb.lk.ahibernate.dao;

import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteOpenHelper;

public interface BaseDao<T> {

	public SQLiteOpenHelper getDbHelper();

	/**
	 *insert(T,true);
	 * 
	 * @param entity
	 * @return
	 */
	public abstract long insert(T entity);

	/**
	 * 
	 * 
	 * @param entity
	 * @param flag
	 *            flagΪtrue id自增长,flagΪfalse.
	 * @return
	 */
	public abstract long insert(T entity, boolean flag);

	public abstract void delete(Object id);

	public abstract void delete(Object... ids);

	public abstract int update(T entity);

	public abstract T get(Object id);

	public abstract List<T> rawQuery(String sql, String[] selectionArgs);

	public abstract List<T> find();

	public abstract List<T> find(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit);

	public abstract boolean isExist(String sql, String[] selectionArgs);

	/**
	 * ����ѯ�Ľ������Ϊ��ֵ��map.
	 * 
	 * @param sql
	 *            ��ѯsql
	 * @param selectionArgs
	 *            ����ֵ
	 * @return ���ص�Map�е�keyȫ����Сд��ʽ.
	 */
	public List<Map<String, String>> query2MapList(String sql,
			String[] selectionArgs);

	/**
	 * ��װִ��sql����.
	 * 
	 * @param sql
	 * @param selectionArgs
	 */
	public void execSql(String sql, Object[] selectionArgs);

}
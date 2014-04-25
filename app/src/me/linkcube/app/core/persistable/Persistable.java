package me.linkcube.app.core.persistable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * 描述如何存储加载和更新数据集的接口
 * 
 * @param <E>
 *            type of item
 */
public interface Persistable<E> {

	/**
	 * 从数据库查询
	 * 
	 * @param readableDatabase
	 * @return cursor
	 */

	Cursor query(SQLiteDatabase readableDatabase);

	/**
	 * 从数据库按条件查询
	 * 
	 * @param readableDatabase
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @return cursor
	 */
	Cursor query(SQLiteDatabase readableDatabase, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy);

	/**
	 * 从数据库查询一个条目
	 * 
	 * @param cursor
	 * @return a single item, read from this row of the cursor
	 */
	E loadFrom(Cursor cursor);

	/**
	 * 向数据库存入数据
	 * 
	 * @param writableDatabase
	 * @param items
	 */
	void store(SQLiteDatabase writableDatabase, List<E> items);

	/**
	 * 向数据库插入一条数据
	 * 
	 * @param writableDatabase
	 * @param selection
	 * @param selectionArgs
	 * @param item
	 */
	void insert(SQLiteDatabase writableDatabase, E item);

	/**
	 * 向数据库更新一条数据
	 * 
	 * @param writableDatabase
	 * @param selection
	 * @param selectionArgs
	 * @param item
	 */
	void update(SQLiteDatabase writableDatabase, E item);

	/**
	 * 从数据库删除一条数据
	 * 
	 * @param writableDatabase
	 * @param selection
	 * @param selectionArgs
	 * @param item
	 */
	void delete(SQLiteDatabase writableDatabase, E item);

	/**
	 * 向数据库中替换一条数据
	 * 
	 * @param writableDatabase
	 * @param item
	 */
	void replace(SQLiteDatabase writableDatabase, E item);

	/**
	 * 清除该表数据库内容
	 * 
	 * @param writableDatabase
	 */
	void clear(SQLiteDatabase writableDatabase);

}

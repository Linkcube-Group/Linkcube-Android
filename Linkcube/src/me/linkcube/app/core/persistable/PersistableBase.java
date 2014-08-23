package me.linkcube.app.core.persistable;

import java.util.List;

import me.linkcube.app.core.Timber;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class PersistableBase<E> implements Persistable<E> {

	protected abstract String getTableName();

	protected abstract String[] getColumns();

	public Cursor query(SQLiteDatabase readableDatabase) {
		return query(readableDatabase, null, null, null, null, null);
	}

	public Cursor query(SQLiteDatabase readableDatabase, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return readableDatabase.query(getTableName(), getColumns(), selection,
				selectionArgs, groupBy, having, orderBy);
	}
	
	public Cursor query(SQLiteDatabase readableDatabase,String[] colums,  String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return readableDatabase.query(getTableName(), colums, selection,
				selectionArgs, groupBy, having, orderBy);
	}

	public abstract ContentValues getContentValues(E item);

	@Override
	public void insert(SQLiteDatabase writableDatabase, E item) {
		writableDatabase.insert(getTableName(), null, getContentValues(item));
	}

	@Override
	public void store(SQLiteDatabase writableDatabase, List<E> items) {
		if (items == null || items.isEmpty()) {
			Timber.d("itmes is empty");
			return;
		}
		for (E item : items) {
			insert(writableDatabase, item);
		}
	}

	@Override
	public void clear(SQLiteDatabase writableDatabase) {
		writableDatabase.delete(getTableName(), null, null);
	}

	@Override
	public void replace(SQLiteDatabase writableDatabase, E item) {
		writableDatabase.replace(getTableName(), null, getContentValues(item));
	}

}

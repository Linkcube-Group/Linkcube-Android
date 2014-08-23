package me.linkcube.app.core.persistable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.linkcube.app.core.Timber;

public class DBCache {

	private final CacheHelper helper;

	public DBCache(CacheHelper helper) {
		this.helper = helper;
	}

	protected SQLiteDatabase getWritable() {
		try {
			return helper.getWritableDatabase();
		} catch (SQLiteException e1) {
			try {
				return helper.getWritableDatabase();
			} catch (SQLiteException e2) {
				return null;
			}
		}
	}

	protected SQLiteDatabase getReadable() {
		try {
			return helper.getReadableDatabase();
		} catch (SQLiteException e1) {
			try {
				return helper.getReadableDatabase();
			} catch (SQLiteException e2) {
				return null;
			}
		}
	}

	public <E> E loadOneFromDB(final PersistableBase<E> persistableBase,
			String selection, String[] selectionArgs) {
		final SQLiteDatabase db = getReadable();
		Cursor cursor = persistableBase.query(db, selection, selectionArgs,
				null, null, null);

		try {
			if (!cursor.moveToFirst())
				return null;

			return persistableBase.loadFrom(cursor);
		} finally {
			cursor.close();
		}

	}

	public <E> List<E> loadFromDB(final PersistableBase<E> persistableBase) {
		return loadFromDB(persistableBase, null, null, null, null, null);
	}

	public <E> List<E> loadFromDB(final PersistableBase<E> persistableBase,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {

		final SQLiteDatabase db = getReadable();

		if (db == null) {
			return Collections.emptyList();
		}

		Cursor cursor = persistableBase.query(db, selection, selectionArgs,
				groupBy, having, orderBy);

		try {
			if (!cursor.moveToFirst())
				return Collections.emptyList();

			List<E> cached = new ArrayList<E>();
			do
				cached.add(persistableBase.loadFrom(cursor));
			while (cursor.moveToNext());
			return cached;
		} finally {
			cursor.close();
		}
	}

	public <E> void store(PersistableBase<E> persistableBase, List<E> items) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.store(db, items);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.e(e, "store items error");
		} finally {
			db.endTransaction();
		}
	}

	public <E> void insert(PersistableBase<E> persistableBase, E item) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.insert(db, item);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d(e, "insert specified item error");
		} finally {
			db.endTransaction();
		}
	}

	public <E> void update(PersistableBase<E> persistableBase, E item) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.update(db, item);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d(e, "update specified item error");
		} finally {
			db.endTransaction();
		}
	}

	public <E> void replace(PersistableBase<E> persistableBase, E item) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.replace(db, item);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d(e, "replace specified item error");
		} finally {
			db.endTransaction();
		}
	}

	public <E> void delete(PersistableBase<E> persistableBase, E item) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.delete(db, item);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d("delete specified item error", e);
		} finally {
			db.endTransaction();
		}
	}
	
	public <E> void deleteOne(PersistableBase<E> persistableBase, E item) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.delete(db, item);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d("delete specified item error", e);
		} finally {
			db.endTransaction();
		}
	}

	public <E> void clear(PersistableBase<E> persistableBase) {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			persistableBase.clear(db);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.d(e, "clear specified table error");
		} finally {
			db.endTransaction();
		}
	}

	public void clearAll() {
		final SQLiteDatabase db = getWritable();
		if (db == null) {
			Timber.d("get writable error");
			return;
		}
		db.beginTransaction();
		try {
			db.delete(DBConst.TABLE_USER.TABLE_NAME, null, null);
			db.delete(DBConst.TABLE_CHAT.TABLE_NAME, null, null);
			db.delete(DBConst.TABLE_FRIEND.TABLE_NAME, null, null);
			db.delete(DBConst.TABLE_FRIEND_REQUEST.TABLE_NAME, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Timber.e(e, "clear data error");
		} finally {
			db.endTransaction();
		}
	}

}
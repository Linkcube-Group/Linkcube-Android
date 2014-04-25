package me.linkcube.app.core.persistable;

import java.io.IOException;
import java.util.List;

import android.content.Context;

/**
 * 数据库管理类
 * 
 * @author orange
 */
public class DataManager {

	private static DataManager instance = null;

	public static DataManager getInstance() {
		if (null == instance) {
			synchronized (DataManager.class) {
				if (instance == null) {
					instance = new DataManager();
				}
			}
		}
		return instance;
	}

	private DBCache dbCache;
	private CacheHelper helperProvider;

	public void initDatabase(Context context) {
		helperProvider = new CacheHelper(context);
		dbCache = new DBCache(helperProvider);
	}

	public <E> List<E> query(PersistableBase<E> persistableBase)
			throws IOException {
		return dbCache.loadFromDB(persistableBase);
	}

	public <E> List<E> query(PersistableBase<E> persistableBase,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) throws IOException {
		return dbCache.loadFromDB(persistableBase, selection, selectionArgs,
				groupBy, having, orderBy);
	}

	public <E> E queryOne(PersistableBase<E> persistableBase, String selection,
			String[] selectionArgs) throws IOException {
		return dbCache.loadOneFromDB(persistableBase, selection, selectionArgs);
	}

	public <E> void insert(PersistableBase<E> persistableBase, E item) {
		dbCache.insert(persistableBase, item);
	}

	public <E> void store(PersistableBase<E> persistableBase, List<E> items) {
		dbCache.store(persistableBase, items);
	}

	public <E> void update(PersistableBase<E> persistableBase, E item) {
		dbCache.update(persistableBase, item);
	}

	public <E> void replace(PersistableBase<E> persistableBase, E item) {
		dbCache.replace(persistableBase, item);
	}

	public <E> void delete(PersistableBase<E> persistableBase, E item) {
		dbCache.delete(persistableBase, item);
	}

	public <E> void clear(PersistableBase<E> persistableBase) {
		dbCache.clear(persistableBase);
	}

	public void clearAll() {
		dbCache.clearAll();
	}

	private DataManager() {

	}

}
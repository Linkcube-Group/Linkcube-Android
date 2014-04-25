package me.linkcube.app.core.persistable;

import java.util.List;

import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.UserEntity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static me.linkcube.app.core.persistable.DBConst.TABLE_USER.*;

public class PersistableUser extends PersistableBase<UserEntity> {

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumns() {
		return new String[] {};
	}

	@Override
	public UserEntity loadFrom(Cursor cursor) {
		UserEntity user = new UserEntity();
		user.setJID(cursor.getString(cursor.getColumnIndex(JID)));
		user.setNickName(cursor.getString(cursor.getColumnIndex(NICK_NAME)));
		user.setUserAvatar(cursor.getBlob(cursor.getColumnIndex(USER_AVATAR)));
		user.setLinkcubeId(cursor.getString(cursor.getColumnIndex(LINKCUBE_ID)));
		user.setPersonState(cursor.getString(cursor
				.getColumnIndex(PERSON_STATE)));
		user.setUserAge(cursor.getString(cursor.getColumnIndex(USER_AGE)));
		user.setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY)));
		user.setAstrology(cursor.getString(cursor.getColumnIndex(ASTROLOGY)));
		user.setUserGender(cursor.getString(cursor.getColumnIndex(USER_GENDER)));
		return user;
	}

	@Override
	public void store(SQLiteDatabase writableDatabase, List<UserEntity> items) {
		if (items == null || items.isEmpty()) {
			Timber.d("items is empty");
			return;
		}
		for (UserEntity user : items) {
			insert(writableDatabase, user);
		}
	}

	/*
	 * @Override public void insert(SQLiteDatabase writableDatabase, UserEntity
	 * item) { if (item == null) { Timber.d("item is empty"); return; }
	 * insert(writableDatabase, item); }
	 */

	@Override
	public void update(SQLiteDatabase writableDatabase, UserEntity user) {
		writableDatabase.update(getTableName(), getContentValues(user), JID
				+ "=?", new String[] { user.getJID() });
	}

	@Override
	public void delete(SQLiteDatabase writableDatabase, UserEntity user) {

	}

	@Override
	public ContentValues getContentValues(UserEntity user) {
		ContentValues values = new ContentValues();
		values.put(JID, user.getJID());
		values.put(NICK_NAME, user.getNickName());
		values.put(USER_AVATAR, user.getUserAvatar());
		values.put(LINKCUBE_ID, user.getLinkcubeId());
		values.put(PERSON_STATE, user.getPersonState());
		values.put(USER_AGE, user.getUserAge());
		values.put(BIRTHDAY, user.getBirthday());
		values.put(ASTROLOGY, user.getAstrology());
		values.put(USER_GENDER, user.getUserGender());
		return values;
	}

}

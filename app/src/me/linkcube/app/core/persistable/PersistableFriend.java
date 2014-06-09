package me.linkcube.app.core.persistable;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.linkcube.app.core.entity.FriendEntity;

public class PersistableFriend extends PersistableBase<FriendEntity> {

	@Override
	public FriendEntity loadFrom(Cursor cursor) {
		FriendEntity friend = new FriendEntity();
		friend.setUserJid(cursor.getString(cursor.getColumnIndex(USER_JID)));
		friend.setFriendJid(cursor.getString(cursor.getColumnIndex(FRIEND_JID)));
		friend.setNickName(cursor.getString(cursor.getColumnIndex(NICK_NAME)));
		friend.setUserAvatar(cursor.getBlob(cursor.getColumnIndex(USER_AVATAR)));
		friend.setPersonState(cursor.getString(cursor
				.getColumnIndex(PERSON_STATE)));
		friend.setUserAge(cursor.getString(cursor.getColumnIndex(USER_AGE)));
		friend.setBirthday(cursor.getString(cursor.getColumnIndex(BIRTHDAY)));
		friend.setAstrology(cursor.getString(cursor.getColumnIndex(ASTROLOGY)));
		friend.setUserGender(cursor.getString(cursor
				.getColumnIndex(USER_GENDER)));
		friend.setIsFriend(cursor.getString(cursor
				.getColumnIndex(IS_FRIEND)));

		return friend;
	}

	@Override
	public void update(SQLiteDatabase writableDatabase, FriendEntity item) {
		writableDatabase.update(getTableName(), getContentValues(item), USER_JID+"=? and "+FRIEND_JID+"=?", new String[]{item.getUserJid(),item.getFriendJid()});
	}

	@Override
	public void delete(SQLiteDatabase writableDatabase, FriendEntity item) {
		writableDatabase.delete(getTableName(), FRIEND_JID + "=? and "
				+ USER_JID + "=?",
				new String[] { item.getFriendJid(), item.getUserJid() });
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumns() {
		return new String[] {};
	}

	@Override
	public ContentValues getContentValues(FriendEntity item) {
		ContentValues values = new ContentValues();
		values.put(USER_JID, item.getUserJid());
		values.put(FRIEND_JID, item.getFriendJid());
		values.put(NICK_NAME, item.getNickName());
		values.put(USER_AVATAR, item.getUserAvatar());
		values.put(PERSON_STATE, item.getPersonState());
		values.put(USER_AGE, item.getUserAge());
		values.put(BIRTHDAY, item.getBirthday());
		values.put(ASTROLOGY, item.getAstrology());
		values.put(USER_GENDER, item.getUserGender());
		values.put(IS_FRIEND, item.getIsFriend());
		return values;
	}

	@Override
	public void deleteOne(SQLiteDatabase writableDatabase, FriendEntity item) {
		
	}

}

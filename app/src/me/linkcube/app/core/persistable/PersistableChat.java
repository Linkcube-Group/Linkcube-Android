package me.linkcube.app.core.persistable;

import static me.linkcube.app.core.persistable.DBConst.TABLE_CHAT.*;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatEntity;

public class PersistableChat extends PersistableBase<ChatEntity> {

	@Override
	public ChatEntity loadFrom(Cursor cursor) {
		ChatEntity chat = new ChatEntity();
		chat.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
		chat.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
		chat.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
		chat.setFriendNickname(cursor.getString(cursor.getColumnIndex(FRIEND_NICKNAME)));
		chat.setMsgTime(cursor.getString(cursor.getColumnIndex(MSG_TIME)));
		chat.setMsgFlag(cursor.getString(cursor.getColumnIndex(MSG_FLAG)));
		return chat;
	}

	@Override
	public void update(SQLiteDatabase writableDatabase, ChatEntity item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(SQLiteDatabase writableDatabase, ChatEntity item) {

		writableDatabase.delete(getTableName(), FRIEND_NAME + "=?",
				new String[] { item.getFriendName() });
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
	public ContentValues getContentValues(ChatEntity item) {
		ContentValues values = new ContentValues();
		values.put(USER_NAME, item.getUserName());
		values.put(FRIEND_NAME, item.getFriendName());
		values.put(FRIEND_NICKNAME, item.getFriendNickname());
		values.put(MSG_FLAG, item.getMsgFlag());
		values.put(MESSAGE, item.getMessage());
		values.put(MSG_TIME, item.getMsgTime());
		return values;
	}

	@Override
	public void store(SQLiteDatabase writableDatabase, List<ChatEntity> items) {
		if (items == null || items.isEmpty()) {
			Timber.d("items is empty");
			return;
		}
		for (ChatEntity user : items) {
			insert(writableDatabase, user);
		}
	}

	@Override
	public Cursor query(SQLiteDatabase readableDatabase, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return super.query(readableDatabase, selection, selectionArgs, groupBy,
				having, orderBy);
	}

}

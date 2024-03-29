package me.linkcube.app.core.persistable;

import static me.linkcube.app.core.persistable.DBConst.TABLE_CHAT.*;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.linkcube.app.common.util.PreferenceUtils;
import me.linkcube.app.core.Timber;
import me.linkcube.app.core.entity.ChatEntity;

public class PersistableChat extends PersistableBase<ChatEntity> {

	@Override
	public ChatEntity loadFrom(Cursor cursor) {
		ChatEntity chat = new ChatEntity();
		chat.setMsgId(cursor.getInt(cursor.getColumnIndex(ID))+"");
		chat.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
		chat.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
		chat.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
		chat.setFriendNickname(cursor.getString(cursor
				.getColumnIndex(FRIEND_NICKNAME)));
		chat.setIsAfterRead(cursor.getInt(cursor.getColumnIndex(IS_AFTER_READ)));
		chat.setMsgTime(cursor.getString(cursor.getColumnIndex(MSG_TIME)));
		chat.setMsgFlag(cursor.getString(cursor.getColumnIndex(MSG_FLAG)));
		return chat;
	}

	@Override
	public void update(SQLiteDatabase writableDatabase, ChatEntity item) {
		writableDatabase.update(getTableName(),getDeleteContentValues(item), FRIEND_NAME + "=? and "
				+ MESSAGE + "=?",
				new String[] { item.getFriendName(), item.getMessage() });
	}

	@Override
	public void delete(SQLiteDatabase writableDatabase, ChatEntity item) {

		writableDatabase.delete(getTableName(), FRIEND_NAME + "=?",
				new String[] { item.getFriendName() });

	}

	@Override
	public void deleteOne(SQLiteDatabase writableDatabase, ChatEntity item) {
		writableDatabase.delete(getTableName(), FRIEND_NAME + "=? and "
				+ MESSAGE + "=?",
				new String[] { item.getFriendName(), item.getMessage() });
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
		values.put(IS_AFTER_READ, item.getIsAfterRead());
		return values;
	}
	
	public ContentValues getDeleteContentValues(ChatEntity item) {
		ContentValues values = new ContentValues();
		values.put(USER_NAME, item.getUserName());
		values.put(FRIEND_NAME, item.getFriendName());
		values.put(FRIEND_NICKNAME, item.getFriendNickname());
		values.put(MSG_FLAG, item.getMsgFlag());
		if(PreferenceUtils.getInt("app_language", 0)==1){
			values.put(MESSAGE, "After Reading Message");
		}else{
			values.put(MESSAGE, "阅后即焚消息");
		}
		values.put(MSG_TIME, item.getMsgTime());
		values.put(IS_AFTER_READ, item.getIsAfterRead());
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

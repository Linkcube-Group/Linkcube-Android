package me.linkcube.app.core.persistable;

import static me.linkcube.app.core.persistable.DBConst.TABLE_FRIEND_REQUEST.*;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import me.linkcube.app.core.entity.FriendRequestEntity;

public class PersistableFriendRequest extends PersistableBase<FriendRequestEntity> {

	@Override
	public FriendRequestEntity loadFrom(Cursor cursor) {
		FriendRequestEntity friendRequest = new FriendRequestEntity();
		friendRequest.setUserName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
		friendRequest.setFriendName(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
		friendRequest.setSubscription(cursor.getString(cursor.getColumnIndex(SUBSCRIPTION)));

		return friendRequest;
	}

	@Override
	public void update(SQLiteDatabase writableDatabase, FriendRequestEntity item) {
		//writableDatabase.update(getTableName(), getContentValues(item), USER_NAME+"='"+item.getUserName()+"' and "+FRIEND_NAME+"='"+item.getFriendName()+"'", null);
		writableDatabase.update(getTableName(), getContentValues(item), USER_NAME+"=? and "+FRIEND_NAME+"=?", new String[]{item.getUserName(),item.getFriendName()});
	}

	@Override
	public void delete(SQLiteDatabase writableDatabase, FriendRequestEntity item) {
		writableDatabase.delete(getTableName(), USER_NAME+"=? and "+FRIEND_NAME+"=?", new String[]{item.getUserName(),item.getFriendName()});
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String[] getColumns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues getContentValues(FriendRequestEntity item) {
		ContentValues values = new ContentValues();
		values.put(USER_NAME, item.getUserName());
		values.put(FRIEND_NAME, item.getFriendName());
		values.put(SUBSCRIPTION, item.getSubscription());
		return values;
	}

	@Override
	public void deleteOne(SQLiteDatabase writableDatabase,
			FriendRequestEntity item) {
		
	}

}

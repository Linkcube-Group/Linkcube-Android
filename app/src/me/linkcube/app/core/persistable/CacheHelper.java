/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.linkcube.app.core.persistable;

import me.linkcube.app.core.Timber;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	private static final String NAME = "cache.db";

	public CacheHelper(final Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		Timber.d(DBConst.TABLE_USER.createSql());
		db.execSQL(DBConst.TABLE_USER.createSql());
		
		Timber.d(DBConst.TABLE_CHAT.createSql());
		db.execSQL(DBConst.TABLE_CHAT.createSql());
		
		Timber.d(DBConst.TABLE_FRIEND.createSql());
		db.execSQL(DBConst.TABLE_FRIEND.createSql());
		
		Timber.d(DBConst.TABLE_FRIEND_REQUEST.createSql());
		db.execSQL(DBConst.TABLE_FRIEND_REQUEST.createSql());
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DBConst.TABLE_USER.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBConst.TABLE_CHAT.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBConst.TABLE_FRIEND.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DBConst.TABLE_FRIEND_REQUEST.TABLE_NAME);
		onCreate(db);
	}
}

package com.nunesrafael.android.checkpoint.datasource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenDataBaseHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_ALLOCATIONS = "allocations";
	public static final String COLUMN_ID         = "id";
	public static final String COLUMN_DATE       = "date";
	public static final String COLUMN_ENTRY_HOUR = "entry_hour";
	public static final String COLUMN_EXIT_HOUR  = "exit_hour";
	public static final String COLUMN_COMMENT    = "comment";
	public static final String COLUMN_CATEGORY   = "category";

	private static final String DATABASE_NAME    = "checkpoint.db";
	private static final int DATABASE_VERSION    = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = " CREATE TABLE  IF NOT EXISTS "
			+ TABLE_ALLOCATIONS + " ( " 
			+ COLUMN_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ COLUMN_DATE       + " DATETIME NOT NULL DEFAULT CURRENT_DATE, "
			+ COLUMN_ENTRY_HOUR + " DATETIME, "
			+ COLUMN_EXIT_HOUR  + " DATETIME, "
			+ COLUMN_COMMENT    + " TEXT, "
			+ COLUMN_CATEGORY   + " TEXT "
			+ " ); ";

	public OpenDataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(OpenDataBaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALLOCATIONS);
		onCreate(db);
	}
}
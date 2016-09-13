package com.wolfie.eskey.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by david on 4/09/16.
 */

public class Helper extends SQLiteOpenHelper {

    private static final String TAG = "Helper";

    public Helper(Context context) {
        super(context, MetaData.DATABASE_NAME, null, MetaData.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + MetaData.ENTRIES_TABLE + " ("
                    + MetaData.ENTRIES_ID + " INTEGER PRIMARY KEY,"
                    + MetaData.ENTRIES_GROUP + " TEXT NOT NULL,"
                    + MetaData.ENTRIES_ENTRY + " TEXT NOT NULL,"
                    + MetaData.ENTRIES_CONTENT + " TEXT NOT NULL);");
            db.execSQL("CREATE TABLE " + MetaData.MASTER_TABLE + " ("
                    + MetaData.MASTER_SALT + " TEXT NOT NULL,"
                    + MetaData.MASTER_KEY + " TEXT NOT NULL);");
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        }

        /*
            private static final String MASTER_KEY_CREATE =
            "create table " + TABLE_MASTER_KEY + " ("
                    + "encryptedkey text not null);";

    private static final String SALT_CREATE =
            "create table " + TABLE_SALT + " ("
                    + "salt text not null);";

         */
    }

    public void dropTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + MetaData.ENTRIES_TABLE);
            db.execSQL("DROP TABLE " + MetaData.MASTER_TABLE);
        } catch (SQLException e) {
            Log.d(TAG, "SQLite exception: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion == 1 && newVersion == 2) {
//            db.execSQL("ALTER ENTRIES_TABLE " + SpendsTableMetaData.SPEND_TABLE_NAME + " ADD COLUMN " + SpendsTableMetaData.SPEND_ACCOUNT + " TEXT");
//            db.execSQL("UPDATE " + SpendsTableMetaData.SPEND_TABLE_NAME + " SET " + SpendsTableMetaData.SPEND_ACCOUNT + " = 'PERSONAL'");
        }
        onCreate(db);
    }
}

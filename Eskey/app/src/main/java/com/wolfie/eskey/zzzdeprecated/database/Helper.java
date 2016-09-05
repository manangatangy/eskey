package com.wolfie.eskey.zzzdeprecated.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wolfie.eskey.zzzdeprecated.provider.MetaData;
import com.wolfie.eskey.zzzdeprecated.provider.TableData;

/**
 * Created by david on 3/09/16.
 */

public class Helper extends SQLiteOpenHelper {

    private static final String TAG = "Helper";

    public Helper(Context context) {
        super(context, MetaData.DATABASE_NAME, null, MetaData.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE ENTRIES_TABLE IF NOT EXISTS " + TableData.Entry.TABLE + " ("
                + TableData.Entry._ID + " INTEGER PRIMARY KEY,"
                + TableData.Entry.GROUP + " TEXT,"
                + TableData.Entry.ENTRY + " TEXT,"
                + TableData.Entry.CONTENT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "upgrading database from version " + oldVersion + " to " + newVersion);
        if (oldVersion == 1 && newVersion == 2) {
//            db.execSQL("ALTER ENTRIES_TABLE " + SpendsTableMetaData.SPEND_TABLE_NAME + " ADD COLUMN " + SpendsTableMetaData.SPEND_ACCOUNT + " TEXT");
//            db.execSQL("UPDATE " + SpendsTableMetaData.SPEND_TABLE_NAME + " SET " + SpendsTableMetaData.SPEND_ACCOUNT + " = 'PERSONAL'");
        }
        //db.execSQL("DROP ENTRIES_TABLE IF EXISTS " + SpendsTableMetaData.TABLE_NAME);
        onCreate(db);
    }
}


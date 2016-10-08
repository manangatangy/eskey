package com.wolfie.eskey.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.MasterData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class Source {

//    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private boolean mAllowEntryAccess;

    public Source(SQLiteDatabase database) {
        mDatabase = database;
    }

    /**
     * Enables read/write of Entry objects.
     * The MasterData-related access is not prevented because access to the MasterData
     * is needed prior to login, in order to authenticate the login.
     */
    public void setAllowEntryAccess(boolean allowEntryAccess) {
        mAllowEntryAccess = allowEntryAccess;
    }
    public boolean getAllowEntryAccess() {
        return mAllowEntryAccess;
    }

//    public Source(Context context) {
//        mHelper = new Helper(context);
//        mDatabase = mHelper.getWritableDatabase();
//    }

    /**
     * @return null if there was an error
     */
    public MasterData readMaster() {
        MasterData masterData = null;
        Cursor cursor = mDatabase.query(MetaData.MASTER_TABLE, MetaData.MASTER_ALL_COLUMNS, null,
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String salt = cursor.getString(cursor.getColumnIndex(MetaData.MASTER_SALT));
            String maskerKey = cursor.getString(cursor.getColumnIndex(MetaData.MASTER_KEY));
            masterData = new MasterData(salt, maskerKey);
            cursor.close();
        }
        return masterData;
    }

    /**
     * @return false if there was an error
     */
    public boolean storeMaster(MasterData masterData) {
        mDatabase.delete(MetaData.MASTER_TABLE, null, null);
        long result = mDatabase.insert(MetaData.MASTER_TABLE, null, makeContentValues(masterData));
        return result != -1;
    }

    public boolean insert(Entry entry) {
        long result = mAllowEntryAccess
                ? mDatabase.insert(MetaData.ENTRIES_TABLE, null, makeContentValues(entry))
                : -1;
        return result != -1;
    }

    public boolean update(Entry entry) {
        int result = mAllowEntryAccess
                ? mDatabase.update(MetaData.ENTRIES_TABLE, makeContentValues(entry),
                    MetaData.ENTRIES_ID + "=" + entry.getId(), null)
                : -1;
        return result != 0;
    }

    public boolean delete(Entry entry) {
        int result = mAllowEntryAccess
                ? mDatabase.delete(MetaData.ENTRIES_TABLE, MetaData.ENTRIES_ID + "=" + entry.getId(), null)
                : -1;
        return result != 0;
    }

    public @NonNull DataSet read() {
        List<Entry> entries = new ArrayList<>();
        if (mAllowEntryAccess) {
            Cursor cursor = mDatabase.query(MetaData.ENTRIES_TABLE, MetaData.ENTRIES_ALL_COLUMNS, null,
                    null, null, null, MetaData.QUERY_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Entry entry = Entry.from(cursor);
                    entries.add(entry);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        DataSet dataSet = new DataSet();
        dataSet.setEntries(entries);
        return dataSet;
    }

    private ContentValues makeContentValues(Entry entry) {
        ContentValues values = new ContentValues();
        values.put(MetaData.ENTRIES_GROUP, entry.getGroupName());
        values.put(MetaData.ENTRIES_ENTRY, entry.getEntryName());
        values.put(MetaData.ENTRIES_CONTENT, entry.getContent());
        return values;
    }

    private ContentValues makeContentValues(MasterData masterData) {
        ContentValues values = new ContentValues();
        values.put(MetaData.MASTER_SALT, masterData.getSalt());
        values.put(MetaData.MASTER_KEY, masterData.getMasterKey());
        return values;
    }
}

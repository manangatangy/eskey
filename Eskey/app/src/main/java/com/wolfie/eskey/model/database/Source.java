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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.spongycastle.asn1.x500.style.RFC4519Style.c;

/**
 * Created by david on 4/09/16.
 */

public class Source {

//    private Helper mHelper;
    private SQLiteDatabase mDatabase;

    public Source(SQLiteDatabase database) {
        mDatabase = database;
    }

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
            // Both fields must be populated.
            if (salt != null && salt.length() > 0 && maskerKey != null && maskerKey.length() > 0) {
                masterData = new MasterData(salt, maskerKey);
            }
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
        long result = mDatabase.insert(MetaData.ENTRIES_TABLE, null, makeContentValues(entry));
        return result != -1;
    }

    public boolean update(Entry entry) {
        int result = mDatabase.update(MetaData.ENTRIES_TABLE, makeContentValues(entry),
                    MetaData.ENTRIES_ID + "=" + entry.getId(), null);
        return result != 0;
    }

    public boolean delete(Entry entry) {
        int result =  mDatabase.delete(MetaData.ENTRIES_TABLE, MetaData.ENTRIES_ID + "=" + entry.getId(), null);
        return result != 0;
    }

    public @NonNull DataSet read() {
        List<Entry> entries = new ArrayList<>();
        // No point sorting here - the strings are encrypted - duh!
        Cursor cursor = mDatabase.query(MetaData.ENTRIES_TABLE, MetaData.ENTRIES_ALL_COLUMNS, null,
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Entry entry = Entry.from(cursor);
                entries.add(entry);
                cursor.moveToNext();
            }
            cursor.close();
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
        values.put(MetaData.MASTER_KEY, masterData.getKey());
        return values;
    }
}

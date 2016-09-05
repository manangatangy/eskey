package com.wolfie.eskey.zzzdeprecated.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.wolfie.eskey.zzzdeprecated.database.Helper;

import java.util.HashMap;

/**
 * Created by david on 3/09/16.
 */

public class EntryProvider extends ContentProvider {

    private static final String TAG = "EntryProvider";

    private static final UriMatcher mUriMatcher;
    private static final int INDICATOR_COLLECTION = 1;
    private static final int INDICATOR_SINGLE = 2;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(MetaData.AUTHORITY, "entries", INDICATOR_COLLECTION);
        mUriMatcher.addURI(MetaData.AUTHORITY, "entries/#", INDICATOR_SINGLE);
    }

    private static HashMap<String, String> mProjectionMap;
    static {
        mProjectionMap = new HashMap<>();
        mProjectionMap.put(TableData.Entry._ID, TableData.Entry._ID);
        mProjectionMap.put(TableData.Entry.GROUP, TableData.Entry.GROUP);
        mProjectionMap.put(TableData.Entry.ENTRY, TableData.Entry.ENTRY);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case INDICATOR_COLLECTION:
                return TableData.Entry.CONTENT_TYPE;
            case INDICATOR_SINGLE:
                return TableData.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Helper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new Helper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch(mUriMatcher.match(uri)) {
            case INDICATOR_SINGLE:
                qb.appendWhere(TableData.Entry._ID + "=" + uri.getPathSegments().get(1));
                // Fall through.
            case INDICATOR_COLLECTION:
                qb.setTables(TableData.Entry.TABLE);
                qb.setProjectionMap(mProjectionMap);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(sortOrder))
            sortOrder = TableData.Entry.DEFAULT_SORT_ORDER;
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        int i = c.getCount();
        Log.w(TAG, "selection:" + selection + ", count=" + i);
        // Tell cursor which uri to watch, so it knows when its source data changes.
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        switch (match) {
            case INDICATOR_SINGLE:
                // Ensure that all the fields are set.
                checkFieldIsPresent(values, TableData.Entry.GROUP, uri);
                checkFieldIsPresent(values, TableData.Entry.ENTRY, uri);
                checkFieldIsPresent(values, TableData.Entry.GROUP, uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mHelper.getWritableDatabase();
        long rowId = db.insert(TableData.Entry.TABLE, null, values);
        if (rowId <= 0)
            throw new SQLException("Failed to insert row into " + uri);

        Log.v(TAG, "inserted: ENTRIES_ID=" + rowId);
        Uri insertedUri = ContentUris.withAppendedId(TableData.Entry.CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(insertedUri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        switch(mUriMatcher.match(uri)) {
            case INDICATOR_SINGLE:
                String rowId1 = uri.getPathSegments().get(1);
                count = db.delete(TableData.Entry.TABLE, whereClause(rowId1, where), whereArgs);
                //Log.v(TAG, "deleted: ENTRIES_ID=" + rowId1);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int count;
        switch(mUriMatcher.match(uri)) {
            case INDICATOR_SINGLE:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(TableData.Entry.TABLE, values, whereClause(rowId, where), whereArgs);
                Log.v(TAG, "updated: ENTRIES_ID=" + rowId);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private void checkFieldIsPresent(ContentValues values, String field, Uri uri) {
        if (!values.containsKey(field))
            throw new SQLException("Failed to insert row because field '" + field + "' is needed, uri ==> " + uri);
    }

    private String whereClause(String rowId, String where) {
        return TableData.Entry._ID + "=" + rowId + (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")");
    }
}

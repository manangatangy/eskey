package com.wolfie.eskey.model;

import android.database.Cursor;

import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.model.database.MetaData;

/**
 * Created by david on 4/09/16.
 */

public class Entry {
    private int mId = -1;
    private String mEntryName;
    private String mGroupName;
    private String mContent;

    private Entry(int id, String entryName, String groupName, String content) {
        mId = id;
        mEntryName = entryName;
        mGroupName = groupName;
        mContent = content;
    }

    public static Entry create(String entryName, String groupName, String content) {
        Entry entry = new Entry(-1, entryName, groupName, content);
        return entry;
    }

    public static Entry from(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(MetaData.ENTRIES_ID));
        String groupName = cursor.getString(cursor.getColumnIndex(MetaData.ENTRIES_GROUP));
        String entryName = cursor.getString(cursor.getColumnIndex(MetaData.ENTRIES_ENTRY));
        String content = cursor.getString(cursor.getColumnIndex(MetaData.ENTRIES_CONTENT));
        Entry entry = new Entry(id, entryName, groupName, content);
        return entry;
    }

    public void encrypt(Crypter crypter) {
        mEntryName = crypter.encrypt(mEntryName);
        mGroupName = crypter.encrypt(mGroupName);
        mContent = crypter.encrypt(mContent);
    }

    public void decrypt(Crypter crypter) {
        mEntryName = crypter.decrypt(mEntryName);
        mGroupName = crypter.decrypt(mGroupName);
        mContent = crypter.decrypt(mContent);
    }

    public int getId() {
        return mId;
    }

    public String getEntryName() {
        return mEntryName;
    }

    public void setEntryName(String entryName) {
        this.mEntryName = entryName;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }
}

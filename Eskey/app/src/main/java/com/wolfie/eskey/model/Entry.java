package com.wolfie.eskey.model;

import com.wolfie.eskey.crypto.Crypter;

/**
 * Created by david on 4/09/16.
 */

public class Entry {
    private int mId = -1;
    private String mEntryName;
    private String mGroupName;
    private String mContent;

    public Entry(int id, String entryName, String groupName, String content) {
        mId = id;
        mEntryName = entryName;
        mGroupName = groupName;
        mContent = content;
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

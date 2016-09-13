package com.wolfie.eskey.model;

/**
 * Created by david on 5/09/16.
 */

/**
 * The salt and master key are held in the database, however the salt is held as plaintext
 * while the masterKey is stored as ciphertext.
 */
public class MasterData {

    private String mSalt;
    private String mMasterKey;

    public MasterData(String salt, String masterKey) {
        mSalt = salt;
        mMasterKey = masterKey;
    }

    public String getSalt() {
        return mSalt;
    }

    public void setSalt(String mSalt) {
        this.mSalt = mSalt;
    }

    public String getMasterKey() {
        return mMasterKey;
    }

    public void setMasterKey(String mMasterKey) {
        this.mMasterKey = mMasterKey;
    }
}

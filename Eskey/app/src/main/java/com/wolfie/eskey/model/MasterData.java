package com.wolfie.eskey.model;

/**
 * Created by david on 5/09/16.
 */

import com.google.gson.annotations.Expose;

/**
 * The salt and master key are held in the database, however the salt is held as plaintext
 * while the master Key is stored as ciphertext.
 */
public class MasterData {

    @Expose
    private String mSalt;
    @Expose
    private String mKey;

    // Needed for serialisation
    public MasterData() {}

    public MasterData(String salt, String key) {
        mSalt = salt;
        mKey = key;
    }

    public String getSalt() {
        return mSalt;
    }

    private void setSalt(String salt) {
        this.mSalt = salt;
    }

    public String getKey() {
        return mKey;
    }

    private void setKey(String key) {
        this.mKey = key;
    }
}

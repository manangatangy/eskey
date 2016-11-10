package com.wolfie.eskey.model;

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

    public String getKey() {
        return mKey;
    }

}

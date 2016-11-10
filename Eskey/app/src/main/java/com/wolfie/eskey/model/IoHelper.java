package com.wolfie.eskey.model;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.util.crypto.SpongyCrypter;

import java.io.InputStreamReader;
import java.util.List;

/**
 * Support for passing data between the database form and the file form.
 * This class also is the structure used for json serialisation.
 */
public class IoHelper {

    private Crypter mMediumCrypter;      // Current session entry crypter, not serialised.

    @Expose
    private MasterData masterData;

    @Expose
    private List<Entry> entries;

    public IoHelper() {
        // No arg ctor for deserialiser.
    }

    public IoHelper(Crypter mediumCrypter) {
        mMediumCrypter = mediumCrypter;
    }

    /**
     * The entries are expected to be encrypted, straight from the database.
     * A non-null MasterData (which is also serialised) means serialise the
     * entries un-decrypted.
     */
    public String export(List<Entry> encryptedEntries, @Nullable MasterData masterData) {
        boolean dontDecrypt = (masterData != null);
        for (int i = 0; i < encryptedEntries.size(); i++) {
            encryptedEntries.get(i).decrypt(dontDecrypt ? null : mMediumCrypter);
        }
        this.masterData = masterData;       // Will be null for exporting cleartext
        this.entries = encryptedEntries;    // Will be decrypted for exporting cleartext
        DataSet.sort(this.entries);         // Do the sort, now that text is decrypted.
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Returns encrypted entries, ready to be placed straight into the database.
     * A non-null password means that the stream is to be decrypted, otherwise
     * it is read as cleartext.
     * To prevent inadvertent loading into the model of cipher text, interpreted as
     * cleartext, check the presence of the masterData in the input stream. If it is
     * present (meaning the stream is ciphertext) but no password is suplied, then
     * throw error.
     */
    public List<Entry> inport(InputStreamReader isr, @Nullable String password)
            throws JsonSyntaxException, JsonIOException, WrongPasswordException,
                MissingPasswordException, UnexpectedClearTextInputException {
        Gson gson = new Gson();
        IoHelper ioHelper = gson.fromJson(isr, IoHelper.class);
        boolean dontDecrypt = (password == null);
        if (ioHelper.masterData == null) {
            // Stream is clear text, was user expecting cipher ?
            if (!dontDecrypt) {
                throw new UnexpectedClearTextInputException();
            }
        } else {
            // Stream is cipher text, must be decrypted.
            if (dontDecrypt) {
                throw new MissingPasswordException();
            } else {
                // Decrypt the input stream entries.
                // Fetch the salt and encrypted master-key from the stream/masterData.
                // Set the user-supplied password and attempt to decrypt the master-key.
                SpongyCrypter strongCrypter = SpongyCrypter.makeStrong(ioHelper.masterData.getSalt(), password);
                String decryptedKey = strongCrypter.decrypt(ioHelper.masterData.getKey());
                Log.d("IoHelper", "inport, attempt is " + (decryptedKey == null));
                if (decryptedKey == null) {
                    throw new WrongPasswordException();
                } else {
                    // Now decrypt the stream/entries using the key they were encrypted with.
                    Crypter crypter = SpongyCrypter.makeMedium(ioHelper.masterData.getSalt(), decryptedKey);
                    for (int i = 0; i < ioHelper.entries.size(); i++) {
                        ioHelper.entries.get(i).decrypt(crypter);
                    }
                }
            }
        }
        // Stream entries have either been decrypted or they were already clear text
        // in the stream, so now encrypt them with the current session entry crypter.
        for (int i = 0; i < ioHelper.entries.size(); i++) {
            ioHelper.entries.get(i).encrypt(mMediumCrypter);
        }
        return ioHelper.entries;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public class WrongPasswordException extends Exception { }
    public class MissingPasswordException extends Exception { }
    public class UnexpectedClearTextInputException extends Exception { }
}

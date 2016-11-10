package com.wolfie.eskey.model.loader;

import android.support.annotation.Nullable;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.util.crypto.SpongyCrypter;

import java.util.List;

/**
 * This is a single purpose loader for reading all entries from the data Source, decrypting
 * them with the old crypter, re-encrypting them with a new password and then writing
 * back to the Source.  The new password/salt are also written to the database.  The
 * entire process is performed on background thread.
 */
public class RemasterLoader {

    // The crypter is used to decrypt entries from the dataBase.
    private Source mDataSource;
    private Crypter mOldMediumCrypter;

    /**
     * Should use the non TimingOutSource since we don't want the op to time out
     * part way through the remastering. The specified Crypter should be one used to
     * decrypt the database entries.
     */
    public RemasterLoader(Source dataSource, Crypter oldMediumCrypter) {
        mDataSource = dataSource;
        mOldMediumCrypter = oldMediumCrypter;
    }

    public void remaster(String newPassword, AsyncListeningTask.Listener<String> listener) {
        new RemasterTask(listener).execute(newPassword);
    }

    private class RemasterTask extends AsyncListeningTask<String, String> {

        public RemasterTask(@Nullable Listener<String> listener) {
            super(listener);
        }
        @Override
        public String runInBackground(String newPassword) {
            DataSet dataSet = mDataSource.read();
            List<Entry> entries = dataSet.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).decrypt(mOldMediumCrypter);          // Decrypt using old crypter
            }
            // Create new master key; generate new salt and master-key.  Then set the
            // user-created password in a strong Crypter and use it to encrypt the master-key.
            // The resulting encrypted master-key and the salt are then re-stored in the database.
            String salt = SpongyCrypter.generateSalt();
            String masterKey = SpongyCrypter.generateMasterKey();
            String encryptedMasterKey = SpongyCrypter.makeStrong(salt, newPassword).encrypt(masterKey);
            MasterData newMasterData = new MasterData(salt, encryptedMasterKey);        // For storing

            // Use the salt and decrypted master-key for a crypter, for use with the entries.
            SpongyCrypter newMediumCrypter = SpongyCrypter.makeMedium(salt, masterKey);
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).encrypt(newMediumCrypter);          // Encrypt using new crypter
            }

            // Now load newMasterData and entries into database.
            mDataSource.deleteAll();
            for (int i = 0; i < entries.size(); i++) {
                mDataSource.insert(entries.get(i));
            }
            mDataSource.storeMaster(newMasterData);

            return "Password saved and " + entries.size() + " entries remastered";
        }
    }

}

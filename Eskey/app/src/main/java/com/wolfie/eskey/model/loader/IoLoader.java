package com.wolfie.eskey.model.loader;

import android.support.annotation.Nullable;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.IoHelper;
import com.wolfie.eskey.model.MasterData;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.util.crypto.Crypter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by david on 17/10/16.
 */

public class IoLoader {

    // The Crypter is used to decrypt entries from the dataBase, exported as cleartext
    // and to encrypt entries into the dataBase, imported as either clear or ciphertext.
    private Source mDataSource;
    private Crypter mMediumCrypter;

    // During export, MasterData is used as part of the exported data.
    private MasterData mMasterData;

    // During import, the password is needed to decrypt the imported cipher text.
    private String mPassword;
    // Cleartext is indicated if we have no password (during import) or no masterdata (during export).
    private boolean mAsClearText;
    // During import/restore, optionally delete existing entries first.
    private boolean mIsOverwrite;

    /**
     * Should use the non TimingOutSource since we don't want the op to time out
     * part way through the i/o.  The specified Crypter should be one used to
     * decrypt the database entries.
     */
    public IoLoader(Source dataSource, Crypter mediumCrypter) {
        mDataSource = dataSource;
        mMediumCrypter = mediumCrypter;
    }

    public class IoResult {
        public String mSuccessMessage;
        public String mFailureMessage;
    }
    public class SuccessResult extends IoResult {
        public SuccessResult(String successMessage) {
            mSuccessMessage = successMessage;
        }
    }
    public class FailureResult extends IoResult {
        public FailureResult(String failureMessage) {
            mFailureMessage = failureMessage;
        }
    }

    /**
     * A null MasterData means to export as clear text. Else also place
     * the MasterData in the export file, along with the encrypted entries.
     * This should be the MasterData that can be used later on during import,
     * to decrypt the export file, i.e., the same MasterData that was loaded
     * from the database initially.
     */
    public void export(@Nullable MasterData masterData,
                       File file, AsyncListeningTask.Listener<IoResult> listener) {
        mMasterData = masterData;
        mAsClearText = (mMasterData == null);
        new ExportTask(listener).execute(file);
    }

    public void inport(@Nullable String password, boolean isOverwrite,
                       File file, AsyncListeningTask.Listener<IoResult> listener) {
        mPassword = password;
        mIsOverwrite = isOverwrite;
        mAsClearText = (mPassword == null);
        new ImportTask(listener).execute(file);
    }

    private class ExportTask extends AsyncListeningTask<File, IoResult> {
        public ExportTask(@Nullable Listener<IoResult> listener) {
            super(listener);
        }
        @Override
        public IoResult runInBackground(File file) {
            DataSet dataSet = mDataSource.read();
            List<Entry> encryptedEntries = dataSet.getEntries();

            String json = new IoHelper(mMediumCrypter).export(encryptedEntries, mMasterData);

            IoResult ioResult = null;
            FileOutputStream fos = null;
            BufferedWriter bw = null;
            try {
                fos = new FileOutputStream(file);
                bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                bw.write(json);
                ioResult = new SuccessResult((mAsClearText ? "Exported " : "Backed up ")
                        + encryptedEntries.size() + " entries from "
                        + (mAsClearText ? "cleartext" : "ciphertext"));
            } catch (FileNotFoundException fnfe) {
                return new FailureResult("FileNotFound opening\n" + file.getName());
            } catch (UnsupportedEncodingException usce) {
                return new FailureResult("UnsupportedEncodingException\n" + file.getPath());
            } catch (IOException ioe) {
                return new FailureResult("IOException writing\n" + file.getPath());
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ioe) {
                    ioResult = new FailureResult("IOException closing\n" + file.getPath());
                    // Won't be returned if exception was thrown prior to the finally clause executing.
                }
            }
            return ioResult;
        }
    }

    private class ImportTask extends AsyncListeningTask<File, IoResult> {
        public ImportTask(@Nullable Listener<IoResult> listener) {
            super(listener);
        }
        @Override
        public IoResult runInBackground(File file) {
            IoResult ioResult = null;
            InputStreamReader isr = null;
            try {
                FileInputStream fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);

                List<Entry> encryptedEntries = new IoHelper(mMediumCrypter).inport(isr, mPassword);

                // Load into database, optionally clearing existing data first. (Retain existing session key).
                if (mIsOverwrite) {
                    mDataSource.deleteAll();
                }
                for (int i = 0; i < encryptedEntries.size(); i++) {
                    mDataSource.insert(encryptedEntries.get(i));
                }
                ioResult = new SuccessResult((mAsClearText ? "Imported" : "Restored")
                        + encryptedEntries.size() + " entries from "
                        + (mAsClearText ? "cleartext" : "ciphertext"));
            } catch (FileNotFoundException fnfe) {
                return new FailureResult("FileNotFound opening\n" + file.getPath());
            } catch (JsonIOException jioe) {
                return new FailureResult("JsonIOException reading\n" + file.getPath());
            } catch (JsonSyntaxException jse) {
                return new FailureResult("JsonSyntaxException parsing\n" + file.getPath());
            } catch (IoHelper.WrongPasswordException jse) {
                return new FailureResult("wrong password for restore file");
            } catch (IoHelper.MissingPasswordException jse) {
                return new FailureResult("import file is encrypted - can only be restored");
            } catch (IoHelper.UnexpectedClearTextInputException jse) {
                return new FailureResult("restore file is not encrypted - can only be imported");
            } finally {
                try {
                    if (isr != null) {
                        isr.close();
                    }
                } catch (IOException ioe) {
                    ioResult = new FailureResult("IOException closing\n" + file.getPath());
                    // Won't be returned if exception was thrown prior to the finally clause executing.
                }
            }
            return ioResult;
        }
    }

}

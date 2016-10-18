package com.wolfie.eskey.model.loader;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.util.crypto.Crypter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by david on 17/10/16.
 */

public class IoLoader {

    private Source mDataSource;
    private Crypter mCrypter;

    public IoLoader(Source dataSource) {
        mDataSource = dataSource;
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
     * A null Crypter is allowed and it will export/import the cipherText.
     */
    public void setCrypter(Crypter crypter) {
        mCrypter = crypter;
    }

    public void export(File file, AsyncListeningTask.Listener<IoResult> listener) {
        new ExportTask(listener).execute(file);
    }

//    public void insert(Entry entry, @Nullable AsyncListeningTask.Listener<Boolean> listener) {
//        new InsertTask(listener).execute(entry);
//    }


    private class ExportTask extends AsyncListeningTask<File, IoResult> {
        public ExportTask(@Nullable Listener<IoResult> listener) {
            super(listener);
        }
        @Override
        public IoResult runInBackground(File file) {
            DataSet dataSet = mDataSource.read();
            List<Entry> entries = dataSet.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).decrypt(mCrypter);
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(entries);

            IoResult ioResult = null;
            FileOutputStream fos = null;
            BufferedWriter bw = null;
            try {
                fos = new FileOutputStream(file);
                bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                bw.write(json);
                ioResult = new SuccessResult("exported " + entries.size() + " entries");
            } catch (FileNotFoundException fnfe) {
                return new FailureResult("FileNotFound opening: " + file.getPath());
            } catch (UnsupportedEncodingException usce) {
                return new FailureResult("UnsupportedEncodingException: " + file.getPath());
            } catch (IOException ioe) {
                return new FailureResult("IOException writing: " + file.getPath());
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ioe) {
                    ioResult = new FailureResult("IOException closing: " + file.getPath());
                    // Won't be returned if exception was thrown prior to the finally clause executing.
                }
            }
            return ioResult;
        }
    }

    private class InsertTask extends AsyncListeningTask<Entry, Boolean> {
        public InsertTask(@Nullable Listener<Boolean> listener) {
            super(listener);
        }
        @Override
        public Boolean runInBackground(Entry entry) {
            entry.encrypt(mCrypter);
            return mDataSource.insert(entry);
        }
    }

}

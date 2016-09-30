package com.wolfie.eskey.presenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wolfie.eskey.model.database.Helper;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.model.loader.EntryLoader;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.view.BaseUi;

public class MainPresenter extends BasePresenter<BaseUi> {

    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private Source mSource;
    private Crypter mCrypter;
    private EntryLoader mEntryLoader;

    // This presenter needs no ui (all the ui is performed by the frags)
    public MainPresenter(BaseUi baseUi) {
        super(baseUi);
    }

    public void init(Context context) {
        mHelper = new Helper(context);
        mDatabase = mHelper.getWritableDatabase();
        mSource = new Source(mDatabase);
        mCrypter = new Crypter();
        mEntryLoader = new EntryLoader(context, mSource, mCrypter);
    }

    public EntryLoader getEntryLoader() {
        return mEntryLoader;
    }
}

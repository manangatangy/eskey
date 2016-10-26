package com.wolfie.eskey.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.wolfie.eskey.model.database.Helper;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.model.database.TimingOutSource;
import com.wolfie.eskey.model.loader.EntryLoader;
import com.wolfie.eskey.model.loader.IoLoader;
import com.wolfie.eskey.model.loader.MasterLoader;
import com.wolfie.eskey.model.loader.RemasterLoader;
import com.wolfie.eskey.util.TimeoutMonitor;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.view.BaseUi;

/**
 * The MainPresenter doesn't use a gui, so the BaseUi parameter to the ctor can be null.
 * It extends BasePresenter simply so that it can be returned by BaseFragment.findPresenter.
 */
public class MainPresenter extends BasePresenter<BaseUi> {

    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private TimingOutSource mTimingOutSource;
    private Source mSource;         // Used for IoLoader where timeouts not wanted.

    private TimeoutMonitor mTimeoutMonitor;
    private MasterLoader mMasterLoader;
    private EntryLoader mEntryLoader;

    // This presenter needs no ui (all the ui is performed by the other frags)
    public MainPresenter(BaseUi baseUi, Context context) {
        super(baseUi);

        mHelper = new Helper(context);
        mDatabase = mHelper.getWritableDatabase();

        mTimeoutMonitor = new TimeoutMonitor();
        mTimingOutSource = new TimingOutSource(mDatabase, mTimeoutMonitor);

        mMasterLoader = new MasterLoader(mTimingOutSource);
        mEntryLoader = new EntryLoader(context, mTimingOutSource);
    }

    public void onUserInteraction() {
        mTimeoutMonitor.onUserInteraction();
    }

    public TimeoutMonitor getTimeoutMonitor() {
        return mTimeoutMonitor;
    }

    public MasterLoader getMasterLoader() {
        return mMasterLoader;
    }

    public EntryLoader getEntryLoader() {
        return mEntryLoader;
    }

    public IoLoader makeIoLoader(Crypter crypter) {
        // This IoLoader is not expected to hang around for long, so it's ok
        // to use a non TimingOutSource to create it.
        return new IoLoader(new Source(mDatabase), crypter);
    }

    public RemasterLoader makeRemasterLoader(Crypter crypter) {
        return new RemasterLoader(new Source(mDatabase), crypter);
    }

    public void setTimeout(int timeoutInMillis) {
        mTimeoutMonitor.setTimeout(timeoutInMillis);
    }

}

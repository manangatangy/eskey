package com.wolfie.eskey.model.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.util.TimeoutMonitor;

/**
 * Uses a TimeoutMonitor to allow or disallow Source operations.  Only the
 * accesses to the entries table are checked in this way.  Access to master
 * table is not inhibited since that would prevent re-logging in after a timeout.
 * Duh
 */
public class TimingOutSource extends Source {

    private TimeoutMonitor mTimeoutMonitor;

    /**
     * This inhibits access to just the Entry records, if timedOut (which is not
     * dependent on the stopTimer call.
     */
    public TimingOutSource(SQLiteDatabase database, TimeoutMonitor timeoutMonitor) {
        super(database);
        mTimeoutMonitor = timeoutMonitor;
    }

    public boolean insert(Entry entry) {
        return mTimeoutMonitor.isTimedOut() ? false : super.insert(entry);
    }

    public boolean update(Entry entry) {
        return mTimeoutMonitor.isTimedOut() ? false : super.update(entry);

    }

    public boolean delete(Entry entry) {
        return mTimeoutMonitor.isTimedOut() ? false : super.delete(entry);

    }

    public @NonNull DataSet read() {
        return mTimeoutMonitor.isTimedOut() ? new DataSet(null) : super.read();
    }
}

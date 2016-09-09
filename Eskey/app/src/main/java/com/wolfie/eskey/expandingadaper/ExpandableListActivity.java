package com.wolfie.eskey.expandingadaper;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.custom.crypto.Crypter;
import com.wolfie.eskey.custom.database.Helper;
import com.wolfie.eskey.custom.database.Source;
import com.wolfie.eskey.custom.loader.AsyncListeningTask;
import com.wolfie.eskey.custom.loader.EntryLoader;
import com.wolfie.eskey.custom.model.Entry;
import com.wolfie.eskey.custom.model.DataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListActivity extends AppCompatActivity {

    ExpandableListAdapter mExpandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.expandable_list_activity);
        ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.list_expandable);
        mExpandableListAdapter = new ExpandableListAdapter(this, prepareListData());
        expandableListView.setAdapter(mExpandableListAdapter);

        findViewById(R.id.button_store_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.button_recreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelper.dropTables(mDatabase);
                mHelper.onCreate(mDatabase);
            }
        });
        findViewById(R.id.button_retrieve_master).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findViewById(R.id.button_insert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEntryLoader.insert(new Entry(0, "entry-name", "group-name", "content"));
            }
        });
        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEntry != null) {
                    mEntry.setEntryName("modified-entry-name");
                    mEntryLoader.update(mEntry);
                }
            }
        });
        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEntry != null) {
                    mEntryLoader.delete(mEntry);
                }
            }
        });
        findViewById(R.id.button_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEntryLoader.read(new AsyncListeningTask.Listener<DataSet>() {
                    @Override
                    public void onCompletion(DataSet dataSet) {
                        for (Entry entry : dataSet.getEntries()) {
                            mEntry = entry;
                            System.out.println(mEntry.getId() + ": " + mEntry.getGroupName() +
                                    " " + mEntry.getEntryName() + " '" + mEntry.getContent() + "'");
                        }
                    }
                });
            }
        });

        mHelper = new Helper(this.getApplicationContext());
        mDatabase = mHelper.getWritableDatabase();
        mSource = new Source(mDatabase);
        mCrypter = new Crypter();
        mEntryLoader = new EntryLoader(this, mSource, mCrypter);
    }

    private com.wolfie.eskey.expandingadaper.DataSet prepareListData() {
        List<String> mGroupNames = new ArrayList<>();
        HashMap<String, List<String>> mChildNamesMap = new HashMap<>();

        // Adding child data
        mGroupNames.add("Top 250");
        mGroupNames.add("Now Showing");
        mGroupNames.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        mChildNamesMap.put(mGroupNames.get(0), top250);
        mChildNamesMap.put(mGroupNames.get(1), nowShowing);
        mChildNamesMap.put(mGroupNames.get(2), comingSoon);

        return new com.wolfie.eskey.expandingadaper.DataSet(mGroupNames, mChildNamesMap);
    }

    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private Source mSource;
    private Crypter mCrypter;
    private EntryLoader mEntryLoader;
    private Entry mEntry;           // Last record to be accessed.

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mHelper.close();
        mSource = null;
        mDatabase = null;
        mHelper = null;
    }
}

package com.wolfie.eskey.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.custom.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.custom.adapter.RecyclerAdapter;
import com.wolfie.eskey.custom.crypto.Crypter;
import com.wolfie.eskey.custom.database.Helper;
import com.wolfie.eskey.custom.database.Source;
import com.wolfie.eskey.custom.loader.AsyncListeningTask;
import com.wolfie.eskey.custom.loader.EntryLoader;
import com.wolfie.eskey.custom.model.DataSet;
import com.wolfie.eskey.custom.model.Entry;
import com.wolfie.eskey.custom.model.EntryGroup;
import com.wolfie.eskey.expandingadaper.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 7/09/16.
 */

public class EntryListActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View mStickyHeaderFrame;
    TextView mStickyHeaderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_list_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mStickyHeaderFrame = findViewById(R.id.sticky_header);
        mStickyHeaderText = (TextView)findViewById(R.id.text_heading);

        mHelper = new Helper(this.getApplicationContext());
        mDatabase = mHelper.getWritableDatabase();
        mSource = new Source(mDatabase);
        mCrypter = new Crypter();
        mEntryLoader = new EntryLoader(this, mSource, mCrypter);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getApplicationContext()) {
                    @Override
                    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                });
//        mAdapter = new RecyclerAdapter(getApplicationContext());
//        mRecyclerView.setAdapter(mAdapter);
        mGroupingRecyclerAdapter = new GroupingRecyclerAdapter();
        mRecyclerView.setAdapter(mGroupingRecyclerAdapter);

//        loadSome();
        loadAdapter();
    }

    private void handlePaymentItemScroll(int position) {
        Object item = mGroupingRecyclerAdapter.getItemAt(position);
        if (item instanceof PaymentSummaryViewModel) {
            PaymentSummaryViewModel model = (PaymentSummaryViewModel) item;
            String headerText = model.getSectionHeaderText();
            if (headerText != null) {
                mTxtStickyItemHeader.setText(headerText);
                mTxtStickyItemHeader.setVisibility(View.VISIBLE);
            } else {
                mTxtStickyItemHeader.setVisibility(View.GONE);
            }
        } else {
            mTxtStickyItemHeader.setVisibility(View.GONE);
        }

    }


    private void loadAdapter() {
        mEntryLoader.read(new AsyncListeningTask.Listener<DataSet>() {
            @Override
            public void onCompletion(DataSet dataSet) {
                List<EntryGroup> groups = EntryGroup.buildGroups(null, dataSet);
                mGroupingRecyclerAdapter.setGroups(groups);
//                mAdapter.setEntries(dataSet.getEntries());


//                for (Entry entry : dataSet.getEntries()) {
//                    System.out.println(entry.getId() + ": " + entry.getGroupName() +
//                            " " + entry.getEntryName() + " '" + entry.getContent() + "'");
//                }
//                for (EntryGroup group : groups) {
//                    System.out.println("----- group " + group.getHeading() + " --------");
//                    for (Entry entry : group.getEntries()) {
//                        System.out.println(entry.getId() + ": " + entry.getGroupName() +
//                                " " + entry.getEntryName() + " '" + entry.getContent() + "'");
//                    }
//                    System.out.println("-----  --------");
//                }
            }
        });
    }

    private void loadSome() {
        for (int e = 5; e < 15; e++) {
            mEntryLoader.insert(new Entry(0, "entry-name-" + e, "group-name-" + 1, "content"));
        }
        for (int e = 5; e < 10; e++) {
            mEntryLoader.insert(new Entry(0, "entry-name-" + e, "group-name-" + 2, "content"));
        }
    }

    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private Source mSource;
    private Crypter mCrypter;
    private EntryLoader mEntryLoader;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mHelper.close();
        mSource = null;
        mDatabase = null;
        mHelper = null;
    }

    private RecyclerView mRecyclerView;
//    private RecyclerAdapter mAdapter;
    private GroupingRecyclerAdapter mGroupingRecyclerAdapter;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_list_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package com.wolfie.eskey.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.adapter.ScrollListeningRecyclerView;
import com.wolfie.eskey.controller.NavigationMenuController;
import com.wolfie.eskey.crypto.Crypter;
import com.wolfie.eskey.database.Helper;
import com.wolfie.eskey.database.Source;
import com.wolfie.eskey.loader.AsyncListeningTask;
import com.wolfie.eskey.loader.EntryLoader;
import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.util.BitmapWorkerTask;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by david on 7/09/16.
 */

public class EntryListActivity
        extends AppCompatActivity
        implements NavigationMenuController.OnNavItemSelectedListener,
        ScrollListeningRecyclerView.ItemScrollListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.navigation_view)
    NavigationView mNavigationView;

    @Bind(R.id.background_image)
    ImageView mBackgroundImageView;

    @Bind(R.id.sticky_header)
    View mStickyHeaderFrame;

    @Bind(R.id.heading_divider_top)
    View mStickyHeaderDividerTop;

    @Bind(R.id.heading_text_view)
    TextView mStickyHeaderText;

    @Bind(R.id.recycler_view)
    ScrollListeningRecyclerView mRecyclerView;

    NavigationMenuController mNavigationMenuController;

    private GroupingRecyclerAdapter mGroupingRecyclerAdapter;
    private Helper mHelper;
    private SQLiteDatabase mDatabase;
    private Source mSource;
    private Crypter mCrypter;
    private EntryLoader mEntryLoader;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();


        // adapt the image to the size of the display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(
                mBackgroundImageView, getResources(), R.drawable.st_basils_cathedral_1, size.x, size.y);

        mNavigationMenuController = new NavigationMenuController(mNavigationView);
        mNavigationMenuController.setOnNavItemSelectedListener(this);

//        mMenu = mNavigationView.getMenu();
//        mMenu.setGroupCheckable(R.id.menu_group_headings, true, true);

        mHelper = new Helper(this.getApplicationContext());
        mDatabase = mHelper.getWritableDatabase();
        mSource = new Source(mDatabase);
        mCrypter = new Crypter();
        mEntryLoader = new EntryLoader(this, mSource, mCrypter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        mRecyclerView.setItemScrollListener(this);
        mGroupingRecyclerAdapter = new GroupingRecyclerAdapter();
        mRecyclerView.setAdapter(mGroupingRecyclerAdapter);

//        loadSome();
        loadAdapter();
    }

    @Override
    public void onItemAlignedToTop(int position) {
        Object item = mGroupingRecyclerAdapter.getItemAt(position);
        String headerText;
        if (item instanceof Entry) {
            Entry entry = (Entry) item;
            headerText = entry.getGroupName();
//            if (headerText == null || headerText.length() == 0) {
//                return;
//            }
        } else {
            headerText = (String) item;
        }
        mStickyHeaderText.setText(headerText);
        mStickyHeaderFrame.setVisibility(View.VISIBLE);
        mStickyHeaderDividerTop.setVisibility(View.GONE);
    }

    private DataSet mDataSet;   // Set by loadAdapter.

    /**
     * Cause a reload of the DataSet from the EntryLoader.
     * This will refresh the heading in the nav menu, which
     * then sets the groups in the adapter and list.
     */
    private void loadAdapter() {
        mEntryLoader.read(new AsyncListeningTask.Listener<DataSet>() {
            @Override
            public void onCompletion(DataSet dataSet) {
                mDataSet = dataSet;
                List<String> headings = EntryGroup.buildHeadingsList(mDataSet);
                mNavigationMenuController.setItemsTexts(headings);
            }
        });
    }

    /**
     * Using the selected nav menu item as the group name, create a subset of
     * Entry's from the DataSet, passing it to the adapter for viewing.
     */
    @Override
    public void onNavItemSelected(String itemText) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        String groupName
                = (NavigationMenuController.ALL_GROUPS_NAV_HEADING.equals(itemText))
                ? null : itemText;
        List<EntryGroup> groups = EntryGroup.buildGroups(groupName, mDataSet);
        mGroupingRecyclerAdapter.setGroups(groups);
        // Briefly hide the sticky heading since its text won't be correctly
        // set until a scroll event occurs.
        mRecyclerView.scrollToPosition(0);
//        mStickyHeaderFrame.setVisibility(View.GONE);
    }

    private void loadSome() {
        for (int e = 1; e < 20; e++) {
            mEntryLoader.insert(new Entry(0, "entry-name-" + e, "group-name-" + 3, "content"));
        }
        for (int e = 10; e < 15; e++) {
            mEntryLoader.insert(new Entry(0, "entry-name-" + e, "group-name-" + 2, "content"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mHelper.close();
        mSource = null;
        mDatabase = null;
        mHelper = null;
    }

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

}

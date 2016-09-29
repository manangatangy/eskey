package com.wolfie.eskey.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.view.adapter.GroupingRecyclerAdapter;
import com.wolfie.eskey.view.adapter.ScrollListeningRecyclerView;
import com.wolfie.eskey.controller.NavigationMenuController;
import com.wolfie.eskey.util.crypto.Crypter;
import com.wolfie.eskey.model.database.Helper;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.fragment.ItemDetailFragment;
import com.wolfie.eskey.model.loader.AsyncListeningTask;
import com.wolfie.eskey.model.loader.EntryLoader;
import com.wolfie.eskey.model.DataSet;
import com.wolfie.eskey.model.Entry;
import com.wolfie.eskey.model.EntryGroup;
import com.wolfie.eskey.util.BitmapWorkerTask;
import com.wolfie.eskey.util.DefaultLayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by david on 7/09/16.
 */

public class EntryListActivity
        extends AppCompatActivity
        implements NavigationMenuController.OnNavItemSelectedListener,
        ScrollListeningRecyclerView.ItemScrollListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.background_image)
    ImageView mBackgroundImageView;

    @BindView(R.id.sticky_header)
    View mStickyHeaderFrame;

    @BindView(R.id.heading_divider_top)
    View mStickyHeaderDividerTop;

    @BindView(R.id.heading_text_view)
    TextView mStickyHeaderText;

    @BindView(R.id.recycler_view)
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
        setContentView(R.layout.activity_navigation);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupItemDetail(null);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

        mNavigationMenuController = new NavigationMenuController(getApplicationContext(), mNavigationView);
        mNavigationMenuController.setOnNavItemSelectedListener(this);

//        mMenu = mNavigationView.getMenu();
//        mMenu.setGroupCheckable(R.id.menu_group_headings, true, true);

        mHelper = new Helper(this.getApplicationContext());
        mDatabase = mHelper.getWritableDatabase();
        mSource = new Source(mDatabase);
        mCrypter = new Crypter();
        mEntryLoader = new EntryLoader(this, mSource, mCrypter);

        mRecyclerView.setLayoutManager(new DefaultLayoutManager(this.getApplicationContext()));
        mRecyclerView.setItemScrollListener(this);
        mGroupingRecyclerAdapter = new GroupingRecyclerAdapter(this.getApplicationContext());
        mGroupingRecyclerAdapter.setOnItemInListClickerListener(new GroupingRecyclerAdapter.OnItemInListClickedListener() {
            @Override
            public void onListItemClick(Entry selectedEntry) {
                popupItemDetail(selectedEntry);
            }
        });
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
     * This will refresh the headings in the nav menu, which
     * then sets the groups in the adapter and list.
     */
    private void loadAdapter() {
        mEntryLoader.read(mDataSetReadListener);
    }

    private AsyncListeningTask.Listener<DataSet> mDataSetReadListener =
            new AsyncListeningTask.Listener<DataSet>() {
                @Override
                public void onCompletion(DataSet dataSet) {
                    mDataSet = dataSet;
                    List<String> headings = EntryGroup.buildHeadingsList(mDataSet);
                    // The below causes a call to onNavItemSelected, for the first menu item.
                    mNavigationMenuController.setItemsTexts(headings);
                }
            };

    /**
     * Using the selected nav menu item as the group name, create a subset of
     * Entry's from the DataSet, passing it to the adapter for viewing.
     */
    @Override
    public void onNavItemSelected(String groupName, boolean changed) {
        // Handle activity_navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        List<EntryGroup> groups = EntryGroup.buildGroups(groupName, mDataSet);
        mGroupingRecyclerAdapter.setGroups(groups);
        // Briefly hide the sticky heading since its text won't be correctly
        // set until a scroll event occurs.
        mRecyclerView.scrollToPosition(0);
//        mStickyHeaderFrame.setVisibility(View.GONE);
    }

    private void loadSome() {
        for (int e = 1; e < 20; e++) {
            mEntryLoader.insert(Entry.create("entry-name-" + e, "group-name-" + 3, "content"));
        }
        for (int e = 10; e < 15; e++) {
            mEntryLoader.insert(Entry.create("entry-name-" + e, "group-name-" + 2, "content"));
        }
    }

    // -----------------------------------------------------------------------------
    private final static String FRAGTAG = "ITEM_DETAIL_FRAGMENT";

    /**
     * Create the ItemDetailFragment, for the specified entry, adding it to the
     * fragment manager.  Then show the action sheet.  On close of the action sheet,
     * the fragment will be removed from the frag manager.
     */
    private void popupItemDetail(@Nullable Entry entry) {
        // TODO - check that there is not one instance already before starting a new one
        final ItemDetailFragment itemDetailFragment = addItemDetailFragment(entry);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                itemDetailFragment.show();
            }
        });
    }

    private ItemDetailFragment addItemDetailFragment(@Nullable Entry entry) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final ItemDetailFragment itemDetailFragment = new ItemDetailFragment();
        // The ItemDetailFragment must be set with its data before onCreateView is called.
        itemDetailFragment.setContext(getApplicationContext());
        itemDetailFragment.setEntry(entry);
        itemDetailFragment.setItemEditListener(new ItemDetailFragment.ItemEditListener() {
            @Override
            public void onHide() {
                // After the sheet closes, remove and delete the fragment
                removeItemDetailFragment();
            }
            @Override
            public void onBackgroundClick() {
                // TODO - maybe this should be disabled, so that user doesn't inadvertently
                // lose their edits.  Note that the close button click listener in ItemDetailFragment
                // should probably do this check and ask for confirmation before moving away.
                // itemDetailFragment.close();
            }
            @Override
            public void onSave(Entry entry, boolean isCreate) {
                if (isCreate) {
                    mEntryLoader.insert(entry, mDataSetReadListener);
                } else {
                    mEntryLoader.update(entry, mDataSetReadListener);
                }
                itemDetailFragment.hide();
            }
            @Override
            public void onDelete(Entry entry) {
                mEntryLoader.delete(entry, mDataSetReadListener);
                itemDetailFragment.hide();
            }
        });
        fragmentManager.beginTransaction().add(android.R.id.content, itemDetailFragment, FRAGTAG).commit();
        return itemDetailFragment;
    }

    private void removeItemDetailFragment() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(FRAGTAG);
            fragmentManager.beginTransaction().remove(fragment).commit();
    }
    // -----------------------------------------------------------------------------

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

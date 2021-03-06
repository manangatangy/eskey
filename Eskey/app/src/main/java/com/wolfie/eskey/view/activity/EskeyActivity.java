package com.wolfie.eskey.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.ListPresenter;
import com.wolfie.eskey.presenter.MainPresenter;
import com.wolfie.eskey.presenter.SettingsPresenter;
import com.wolfie.eskey.util.TimeoutMonitor;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.FileFragment;
import com.wolfie.eskey.view.fragment.HelpFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.LoginFragment;
import com.wolfie.eskey.view.fragment.SettingsFragment;

import butterknife.BindView;

public class EskeyActivity extends SimpleActivity {

    @BindView(R.id.layout_activity_drawer)
    public DrawerLayout mDrawer;

    private MainPresenter mMainPresenter;

    @Override
    public MainPresenter getPresenter() {
        return mMainPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainPresenter = new MainPresenter(null, getApplicationContext());

        // Set the initial values for some settings.  May be changed later by SettingsPresenter
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int sessionTimeout = prefs.getInt(SettingsPresenter.PREF_SESSION_TIMEOUT, TimeoutMonitor.DEFAULT_TIMEOUT);
        mMainPresenter.setTimeout(sessionTimeout, false);       // No need to start; we are not yet logged in
        int enumIndex = prefs.getInt(SettingsPresenter.PREF_SESSION_BACKGROUND_IMAGE, SimpleActivity.DEFAULT_BACKGROUND_IMAGE);
        setBackgroundImage(enumIndex);

        // Create the main content fragment into it's container.
        setupFragment(ListFragment.class.getName(), R.id.fragment_container_activity_simple, null);

        // Create the drawer fragment into it's container.
        setupFragment(DrawerFragment.class.getName(), R.id.fragment_container_activity_drawer, null);

        // Create the entry edit (activity sheet) fragment into it's container.
        setupFragment(EditFragment.class.getName(), R.id.fragment_container_edit, null);

        // Create the login (activity sheet) fragment into it's container.
        setupFragment(LoginFragment.class.getName(), R.id.fragment_container_login, null);

        // Create the file (activity sheet) fragment into it's container.
        setupFragment(FileFragment.class.getName(), R.id.fragment_container_file, null);

        // Create the help (activity sheet) fragment into it's container.
        setupFragment(HelpFragment.class.getName(), R.id.fragment_container_help, null);

        // Create the settings (activity sheet) fragment into it's container.
        setupFragment(SettingsFragment.class.getName(), R.id.fragment_container_settings, null);

    }

    @Override
    public void onUserInteraction() {
        mMainPresenter.onUserInteraction();
    }

    @Override
    @LayoutRes
    public int getLayoutResource() {
        // Specify the layout to use for the EskeyActivity.  This layout include
        // the activity_simple layout, which contains the toolbar and the
        // fragment_container_activity_simple container (for ListFragment) as
        // well as fragment_container_activity_drawer for the DrawerFragment
        return R.layout.activity_drawer;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        ListPresenter listPresenter = findPresenter(ListFragment.class);
        SearchViewHandler searchViewHandler = new SearchViewHandler(listPresenter);

        // Retrieve the SearchView and setup the callbacks.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(searchViewHandler);
        searchView.setOnSearchClickListener(searchViewHandler);
        MenuItemCompat.setOnActionExpandListener(searchItem, searchViewHandler);

        return true;
    }

    /**
     * The behaviour of the SearchView is as follows:
     * When the view is open, then the cross in the right hand side will only appear if there is
     * some text in the field.  Clicking this cross will then clear the text but won't close (which
     * is called iconify in the SearchView code) the searchView.  To iconify, must either click
     * back-press (after the keyboard is first hidden), or must click the left arrow in the top
     * left of the app bar.  When either of these is done, then the onMenuItemActionCollapse
     * is called.
     * ref: http://stackoverflow.com/a/18186164
     */
    private class SearchViewHandler implements
            MenuItemCompat.OnActionExpandListener,
            SearchView.OnQueryTextListener,
            View.OnClickListener {

        private SearchListener mSearchListener;

        public SearchViewHandler(SearchListener searchListener) {
            mSearchListener = searchListener;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            mSearchListener.onQueryClose();
            return true;  // Return true to collapse action view
        }

        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;  // Return true to expand action view
        }

        @Override
        public void onClick(View v) {
            mSearchListener.onQueryClick();
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mSearchListener.onQueryTextChange(newText);
            return true;
        }
    }

    public interface SearchListener {
        void onQueryClick();
        void onQueryTextChange(String newText);
        void onQueryClose();
    }
}

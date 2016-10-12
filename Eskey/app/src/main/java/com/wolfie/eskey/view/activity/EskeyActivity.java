package com.wolfie.eskey.view.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.wolfie.eskey.R;
import com.wolfie.eskey.presenter.MainPresenter;
import com.wolfie.eskey.view.fragment.EditFragment;
import com.wolfie.eskey.view.fragment.ListFragment;
import com.wolfie.eskey.view.fragment.DrawerFragment;
import com.wolfie.eskey.view.fragment.LoginFragment;

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
        setBackgroundImage(R.drawable.st_basils_cathedral_1);

        mMainPresenter = new MainPresenter(null, getApplicationContext());

        // Create the main content fragment into it's container.
        setupFragment(ListFragment.class.getName(), R.id.fragment_container_activity_simple, null);

        // Create the drawer fragment into it's container.
        setupFragment(DrawerFragment.class.getName(), R.id.fragment_container_activity_drawer, null);

        // Create the entry edit (activity sheet) fragment into it's container.
        setupFragment(EditFragment.class.getName(), R.id.fragment_container_edit, null);

        // Create the login (activity sheet) fragment into it's container.
        setupFragment(LoginFragment.class.getName(), R.id.fragment_container_login, null);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eskey_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_item_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        resetDisconnectTimer();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        stopDisconnectTimer();
//    }
}

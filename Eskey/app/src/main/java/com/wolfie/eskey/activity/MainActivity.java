package com.wolfie.eskey.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.wolfie.eskey.R;
import com.wolfie.eskey.expandingadaper.ExpandableListAdapter;
import com.wolfie.eskey.expandingadaper.DataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /*

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.list_expandable);
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(this, prepareListData());
        expandableListView.setAdapter(expandableListAdapter);
    }

    private DataSet prepareListData() {
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

        return new DataSet(mGroupNames, mChildNamesMap);
    }
}

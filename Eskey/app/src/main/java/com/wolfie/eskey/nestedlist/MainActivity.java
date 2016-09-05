package com.wolfie.eskey.nestedlist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.wolfie.eskey.R;

/**
 * Created by david on 1/09/16.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nested_list_main);
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_view);
        Adapter adapter = new Adapter(this, new DataManager(), "", null);
        expandableListView.setAdapter(adapter);
    }
}

package com.wolfie.eskey.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class DataSet {

    private List<Entry> mEntries = new ArrayList<>();

    public List<Entry> getEntries() {
        return mEntries;
    }

    public void setEntries(List<Entry> pEntries) {
        this.mEntries = pEntries;
    }

}

package com.wolfie.eskey.custom.model;

import com.wolfie.eskey.custom.loader.DataLoader;

import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class DataSet implements DataLoader.Loadable {

    private List<Entry> pEntries;

    public List<Entry> getEntries() {
        return pEntries;
    }

    public void setEntries(List<Entry> pEntries) {
        this.pEntries = pEntries;
    }

    @Override
    public boolean isEmpty() {
        return pEntries == null || pEntries.size() == 0;
    }

    @Override
    public void clear() {
        if (pEntries != null) {
            pEntries.clear();
        }

    }
}

package com.wolfie.eskey.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by david on 4/09/16.
 */

public class DataSet {

    private List<Entry> mEntries = new ArrayList<>();

    public List<Entry> getEntries() {
        return mEntries;
    }

    public DataSet(List<Entry> pEntries) {
        this.mEntries = pEntries;
    }

    public static void sort(List<Entry> entries) {
        Collections.sort(entries, new Comparator<Entry>() {
            /**
             * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
             *         equal, and > 0 if {@code lhs} is greater than {@code rhs}.
             */
            @Override
            public int compare(Entry lhs, Entry rhs) {
                int compare = lhs.getGroupName().compareToIgnoreCase(rhs.getGroupName());
                if (compare == 0) {
                    compare = lhs.getEntryName().compareToIgnoreCase(rhs.getEntryName());
                }
                return compare;
            }
        });
    }

    public static void sortOnName(List<Entry> entries) {
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                return lhs.getEntryName().compareToIgnoreCase(rhs.getEntryName());
            }
        });
    }
}

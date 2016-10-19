package com.wolfie.eskey.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by david on 18/10/16.
 */

public class IoDataTest {

    @Test
    public void convert() {
        IoHelper ioData = new IoHelper();
//        ioData.setOne("two");
//        ioData. = new MasterData("salty", "masterful");
//        ioData.entries = new ArrayList<Entry>();
//        ioData.entries.add(Entry.create("11", "ww", "dd"));
//        ioData.entries.add(Entry.create("22", "xx", "ee"));
//        ioData.entries.add(Entry.create("33", "yy", "ff"));

        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(ioData);

        System.out.println(json);

        IoHelper ioData2 = gson.fromJson(json, IoHelper.class);
        System.out.println(ioData2);
    }

}

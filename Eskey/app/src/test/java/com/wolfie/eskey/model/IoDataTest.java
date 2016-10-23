package com.wolfie.eskey.model;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wolfie.eskey.model.database.Source;
import com.wolfie.eskey.util.StringUtils;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void convertFromCsv() throws IOException {
        // located at app/src/test/resources/
        IoHelper ioHelper = new IoHelper();
        List<Entry> entries = new ArrayList<>();

        InputStream is = getClass().getClassLoader().getResourceAsStream("oisafe.csv");
        CSVReader reader = new CSVReader(new InputStreamReader(is));

        for (;;) {
            String csv[] = reader.readNext();
            if (csv == null)
                break;
            // "Category","Description","Website","Username","Password","Notes","Last edited"
            //  0           1               2       3           4           5       6
            String name, group, content;
            group = csv[0];
            //              category
            if (StringUtils.isNotBlank(csv[1])) {
                name = csv[1];
                //              description
                content =
                        //              password username website notes
                        csv[4] +
                                (StringUtils.isNotBlank(csv[3]) ? ("\n" + csv[3]) : "") +
                                (StringUtils.isNotBlank(csv[2]) ? ("\n" + csv[2]) : "") +
                                (StringUtils.isNotBlank(csv[5]) ? ("\n" + csv[5]) : "");
            } else {
                name = csv[2];
                content =
                        csv[4] +
                                (StringUtils.isNotBlank(csv[3]) ? ("\n" + csv[3]) : "") +
                                (StringUtils.isNotBlank(csv[2]) ? ("\n" + csv[2]) : "") +
                                (StringUtils.isNotBlank(csv[5]) ? ("\n" + csv[5]) : "");

            }


            Entry entry = Entry.create(name, group, content);
            entries.add(entry);
        }

        String json = ioHelper.export(entries, new MasterData());
        System.out.println(json);

        /*

          reader = new CSVReader(new FileReader(file));

      // Use the first line to determine the type of csv file.  Secrets will
      // output 5 columns, with the names as used in the exportSecrets()
      // function.  OI Safe 1.1.0 is also detected.
      String headers[] = reader.readNext();
      if (null != headers) {
        isSecretsScv = isSecretsCsv(headers);
        if (!isSecretsScv)
          isOiSafeCsv = isOiSafeCsv(headers);
      }

      // Read all the rest of the lines as secrets.
      for (;;) {
        String[] row = reader.readNext();
        if (null == row)
          break;

        Secret secret = new Secret();
        if (isOiSafeCsv) {
          secret.setDescription(row[1]);
          secret.setUsername(row[3]);
          secret.setPassword(row[4], false);
          secret.setEmail(EMPTY_STRING);

         */
    }

    @Test
    public void compare() {
        // located at app/src/test/resources
        InputStream is = getClass().getClassLoader().getResourceAsStream("eskey-clear.txt");
        Gson gson = new Gson();
        IoHelper ioHelper = gson.fromJson(new InputStreamReader(is), IoHelper.class);

        DataSet.sort(ioHelper.getEntries());

        for (Entry e : ioHelper.getEntries()) {
            System.out.println(e.getGroupName() + "    " + e.getEntryName());
        }
    }

    public void reformat() {

    }
}

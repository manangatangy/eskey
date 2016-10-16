package com.wolfie.eskey.presenter;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.wolfie.eskey.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by david on 16/10/16.
 */

@RunWith(AndroidJUnit4.class)
public class FilePresenterTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context context = InstrumentationRegistry.getTargetContext();
        assertEquals("com.wolfie.eskey", context.getPackageName());

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Log.d("test", "getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) ==> " + directory.getPath());
            write(new File(directory, "test1.txt"));
            // This path is /storage/emulated/0/Downloads
            // It is the same location accessed by various file browsers and is also visible
            // when the usb port is mounted to the host for read/writing.
            // Trying to access this location from the code causes EACCES (Permission Denied)

            File directory2 = context.getExternalFilesDir(null);
            Log.d("test", "getExternalFilesDir(null) ==> " + directory2.getPath());
            write(new File(directory2, "test12.txt"));
            // This path is /storage/emulated/0/Android/data/com.wolfie.eskey/files/test1.txt
            // and the file can be written by the activity without asking for permission.
            // However the file is not accessible from file browsers.
            // It can be accessed using adb from the host computer if the device has debug enabled.
            // $ adb pull /storage/emulated/0/Android/data/com.wolfie.eskey/files/test12.txt local_file.txt
            // and $ adb push settings.gradle /storage/emulated/0/Android/data/com.wolfie.eskey/files/.

        }

    }

    public void write(File file) {
        Log.d("test", "writing ==> " + file.getPath() + "   " + file.getName());
        try {
            OutputStream os = new FileOutputStream(file);
            os.write("hello world".getBytes());
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    public void check() {

        // Call to see if the permission is (still) granted.
        //int permissionCheck = ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.WRITE_CALENDAR);
        // Can return PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED
        // If denied,
    }

    // 1. if media is not avalable, disable buttons except cancel
    //
}

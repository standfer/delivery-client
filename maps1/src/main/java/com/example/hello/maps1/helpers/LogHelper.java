package com.example.hello.maps1.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogHelper {
    public static final String TAG = LogHelper.class.getSimpleName();

    public static void createLogFile() {
        try {
            if (isExternalStorageWritable()) {

                File appDirectory = new File(Environment.getExternalStorageDirectory() + "/Log_Delivery");
                File logDirectory = new File(appDirectory + "/log");
                File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");

                // create app folder
                if (!appDirectory.exists()) {
                    appDirectory.mkdir();
                }

                // create log folder
                if (!logDirectory.exists()) {
                    logDirectory.mkdir();
                }

                // clear the previous logcat and then write the new one to the file
                try {
                    Process process = Runtime.getRuntime().exec("logcat -c");
                    process = Runtime.getRuntime().exec("logcat -f " + logFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (isExternalStorageReadable()) {
                // only readable
            } else {
                // not accessible
            }
        } catch (Throwable ex) {
            Log.e(TAG, "Failed to save logs to SD card");
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals( state ) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) ) {
            return true;
        }
        return false;
    }

    public static void sendLogsEmail(Context context) {
        try {
            //send file using email
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            // Set type to "email"
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {"standfer231@gmail.com"};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, getFiles(new File(Environment.getExternalStorageDirectory() + "/Log_Delivery/log")));
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } catch (Throwable ex) {
            Log.e(TAG, "Failed to send logs to email");
        }
    }

    public static ArrayList<Uri> getFiles(final File folder) {
        ArrayList<Uri> files = new ArrayList<>();

        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFiles(file));
            } else {
                files.add(Uri.fromFile(file));
            }
        }

        return files;
    }

    public static List<String> getFilePaths(final File folder) {
        List<String> filePaths = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                filePaths.addAll(getFilePaths(fileEntry));
            } else {
                filePaths.add(fileEntry.getAbsolutePath());
            }
        }

        return filePaths;
    }
}

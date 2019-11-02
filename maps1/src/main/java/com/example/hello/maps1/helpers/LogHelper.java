package com.example.hello.maps1.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.FileProvider;

public class LogHelper {
    public static final String TAG = LogHelper.class.getName();

    public static void createLogFile(boolean isNeedClearLogs) {
        createLogFile(isNeedClearLogs, null);
    }

    public static void createLogFile(boolean isNeedClearLogs, String fileNamePostfix) {
        try {
            if (isExternalStorageWritable()) {
                File appDirectory = new File(Environment.getExternalStorageDirectory() + "/Log_Delivery");
                File logDirectory = new File(appDirectory + "/log");
                File logFile = new File(logDirectory, String.format("logcat_%s%s.log",
                        !StringHelper.isEmpty(fileNamePostfix) ? fileNamePostfix + "_" : "",
                        DateTimeHelper.getCurrentDateTimeFormatted()));

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
                    if (isNeedClearLogs) {
                        Runtime.getRuntime().exec("logcat -c");
                    }
                    Runtime.getRuntime().exec("logcat -f " + logFile);
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
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void sendLogsEmail(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        try {
            createLogFile(false);//todo wait for log creating
            Thread.sleep(5000);
            //send file using email
            // Set type to "email"
            emailIntent.setType("*/*");
            String to[] = {"standfer231@gmail.com"};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, getFiles(new File(Environment.getExternalStorageDirectory() + "/Log_Delivery/log"), context));
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Logs " + DateTimeHelper.getCurrentDateTimeFormatted());
            startEmailActivity(context, emailIntent);
        } catch (Throwable ex) {
            Log.e(TAG, "Failed to send logs to email", ex);
        }
    }

    public static void startEmailActivity(Context context, Intent emailIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            } catch (FileUriExposedException fileUriExposedException) {
                Log.e(TAG, "Failed to open email activity. Trying to open activity in strict mode", fileUriExposedException);
                enableStrictMode();
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        } else {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    public static ArrayList<Uri> getFiles(final File folder, Context context) {
        ArrayList<Uri> files = new ArrayList<>();

        for (final File file : folder.listFiles()) {
            if (file.isDirectory()) {
                files.addAll(getFiles(file, context));
            } else {
                files.add(FileProvider.getUriForFile(
                        context,
                        "com.example.hello.maps1.provider", //(use your app signature + ".provider" )
                        file)
                );
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

    public static void enableStrictMode() { //for not creating signature and uri.fileProvider (https://stackoverflow.com/questions/48117511/exposed-beyond-app-through-clipdata-item-geturi?rq=1)
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }
}

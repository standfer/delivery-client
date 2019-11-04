package com.example.hello.maps1.helpers.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.constants.Constants;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHelper {
    
    public static void initMainActivityPermissions(MainMapsActivity mainMapsActivity) {
        int permissionLocationStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarceLocationStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCallPhoneStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.
                permission.CALL_PHONE);
        int permissionInternetStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.INTERNET);
        int permissionWakeLockStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.WAKE_LOCK);
        int permissionForegroundStatus = ContextCompat.checkSelfPermission(mainMapsActivity, "android.permission.FOREGROUND_SERVICE");

        int permissionReadLogsStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.READ_LOGS);
        int permissionReadExternalStorageStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorageStatus = ContextCompat.checkSelfPermission(mainMapsActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionLocationStatus != PackageManager.PERMISSION_GRANTED ||
                permissionCoarceLocationStatus != PackageManager.PERMISSION_GRANTED ||
                permissionCallPhoneStatus != PackageManager.PERMISSION_GRANTED ||
                permissionInternetStatus != PackageManager.PERMISSION_GRANTED ||
                permissionWakeLockStatus != PackageManager.PERMISSION_GRANTED ||
                permissionReadLogsStatus != PackageManager.PERMISSION_GRANTED ||
                permissionReadExternalStorageStatus != PackageManager.PERMISSION_GRANTED ||
                permissionWriteExternalStorageStatus != PackageManager.PERMISSION_GRANTED ||
                permissionForegroundStatus != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(MainMapsActivity.TAG, "Permissions requested");
            ActivityCompat.requestPermissions(mainMapsActivity, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.READ_LOGS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "android.permission.FOREGROUND_SERVICE"

                    }, Constants.REQUEST_CODE_PERMISSION_ALL);
        }
    }
}

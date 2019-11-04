package com.example.hello.maps1.helpers.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.helpers.ToolsHelper;

/**
 * Created by Ivan on 19.03.2017.
 */

public class StandardIntentsHelper {

    public static void showPhoneIntent(Activity activity, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        activity.startActivity(callIntent);
    }

    public static void checkGpsEnabled(LocationManager locationManager, Toast toast) {
        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                toast.setText("Для работы с программой необходимо включить GPS!");
                toast.show();
                //show activity for settings GPS
                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }
}

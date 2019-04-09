package com.example.hello.maps1.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.hello.maps1.entities.Order;

import java.util.List;

/**
 * Created by Ivan on 02.09.2017.
 */

public class ToolsHelper {

    public static void showMsgToUser(String msg, Toast toast) {
        toast.setText(msg);
        toast.show();
    }

    public static void logException(Throwable ex) {
        Log.d("Unknown exception", ex.toString());
        //throw new RuntimeException("Unknown exception", ex);//todo show error to user
    }

    public static String getLogTagByClass(Class object, String methodName) {
        return String.valueOf(object + ":" + methodName);
    }

    public static void logOrders(Class object, String methodName, List<Order> ... ordersLists) {
        String tag = getLogTagByClass(object, methodName);
        String message = "";

        for (List<Order> orders : ordersLists) {
            message += !CollectionsHelper.isEmpty(orders) ? orders.size() : null;
        }

        Log.d(tag, message);
    }

    public static boolean isGpsPermissioned(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    /*protected void enableGps() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (!provider.contains("gps")) {
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");

            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }*/
}

package com.example.hello.maps1.helpers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.location.Location;
import android.widget.Toast;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static com.example.hello.maps1.services.LocationUpdatesService.KEY_REQUESTING_LOCATION_UPDATES;

/**
 * Created by Ivan on 02.09.2017.
 */

public class ToolsHelper {
    protected static final String TAG = ToolsHelper.class.getName();

    public static void showMsgToUser(String msg, Toast toast) {
        toast.setText(msg);
        toast.show();
    }

    public static void logException(Throwable ex) {//todo move to LogHelper
        Log.d(TAG, ex.toString());
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

    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" :
                "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    public static String getLocationLastUpdate(Context context) {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    public static void initSharedPreferences(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) context);
    }

    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The {@link Context}.
     */
    public static boolean isRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    public static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
                .apply();
    }

    public static void showLocation(Location location, Toast toast, MainMapsActivity mainMapsActivity) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            toast.setText("GPS provider" + location.toString());
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            toast.setText("Network provider" + location.toString());
            ;
        }
        toast.show();
        mainMapsActivity.getMrkCurrentPos().setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        //отправляем объект с текущими координатами на сервер
        String locationToServer = new Coordinate(location).toJson();
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

package com.example.hello.maps1.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.activities.ActivityHelper;
import com.example.hello.maps1.helpers.activities.NotificationHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.services.restarters.TrackingRestarterBroadcastReceiver;

/**
 * Created by Ivan on 19.01.2019.
 */

public class TrackingService extends Service {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Courier courier;
    private boolean isTaskRemovedProcessed = false;
    private boolean isOnDestroyProcessed = false;
    private boolean isNeedToClose = false;

    public TrackingService(Context applicationContext) {
        super();
        Log.i(TrackingService.class.getName(), "Tracking service constructor");
    }

    public TrackingService() {
    }

    private void startInForeground() {
        Notification notification = NotificationHelper.createServiceNotification(this);
        startForeground(Constants.ONGOING_NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TrackingService.class.getName(), "Tracking service onStartCommand!!");
        super.onStartCommand(intent, flags, startId);
        if (intent == null || intent.getExtras() == null) return START_STICKY;

        this.courier = (Courier) ActivityHelper.getFromIntent(intent, Courier.class);
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        this.locationListener = new DeliveryLocationListener(courier);

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                } catch (SecurityException ex) {
                    ToolsHelper.logException(ex);
                }
            }
        });
        startInForeground();

        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i(TrackingService.class.getName(), "Tracking service onStop!!");
        return super.stopService(name);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TrackingService.class.getName(), "Tracking service onTaskRemoved!!");
        super.onTaskRemoved(rootIntent);
        /*isTaskRemovedProcessed = true;

        if (!isOnDestroyProcessed && !isNeedToClose) {
            restartService();
        }*/
    }

    @Override
    public void onDestroy() {
        /*isOnDestroyProcessed = true;

        if (!isTaskRemovedProcessed && !isNeedToClose) {
            restartService();
        }*/
        Log.i(TrackingService.class.getName(), "Tracking service onDestroy!!");
        super.onDestroy();
    }

    public void restartService() {
        //todo add stopper current service actions here
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        Intent broadcastIntent = new Intent(this, TrackingRestarterBroadcastReceiver.class);
        ActivityHelper.putToIntent(broadcastIntent, (Object) courier);
        sendBroadcast(broadcastIntent);
    }

    public void close() {
        Log.i(TrackingService.class.getName(), "Tracking service onDestroy!!");
        this.isNeedToClose = true;
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }
}

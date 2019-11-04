package com.example.hello.maps1.services.restarters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.activities.ActivityHelper;
import com.example.hello.maps1.services.TrackingService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TrackingRestarterBroadcastReceiver.class.getName(), "AlarmReceiver onReceive");
        Courier courier = (Courier) ActivityHelper.getFromIntentBytes(intent, Courier.class);

        ActivityHelper.startService(context, courier, TrackingService.class);
    }
}

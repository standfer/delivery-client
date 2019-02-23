package com.example.hello.maps1.services.restarters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.services.TrackingService;

/**
 * Created by Ivan on 19.01.2019.
 */

public class TrackingRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TrackingRestarterBroadcastReceiver.class.getSimpleName(), "TrackingService stops! Oooooooooooooppppssssss!!!!");
        Intent intentRestore = new Intent(new Intent(context, TrackingService.class));
        Courier courier = (Courier) ActivityHelper.getFromIntent(intent, Courier.class);
        ActivityHelper.putToIntent(intentRestore, courier);

        context.startService(intentRestore);
    }
}

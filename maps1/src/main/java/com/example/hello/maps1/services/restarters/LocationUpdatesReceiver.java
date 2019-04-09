package com.example.hello.maps1.services.restarters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.services.LocationUpdatesService;

public class LocationUpdatesReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
        if (location != null) {
            Toast.makeText(MainMapsActivity.this, Utils.getLocationText(location),
                    Toast.LENGTH_SHORT).show();
        }*/
    }
}

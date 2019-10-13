package com.example.hello.maps1.services;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.example.hello.maps1.helpers.ToolsHelper;

public class TrackingAsyncTask extends AsyncTask<TrackingService, Void, Void> {
    protected TrackingService trackingService;
    protected LocationManager locationManager;

    @Override
    protected Void doInBackground(TrackingService... trackingServices) {
        this.trackingService = trackingServices[0];
        this.locationManager = trackingService.getLocationManager();

        if (ActivityCompat.checkSelfPermission(trackingService.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(trackingService.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }

        return null;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            try {

                Log.i(TrackingService.class.getName(), "Tracking service onLocationChanged");
                /*Coordinate changedLocation = new Coordinate(location);

                courier.setCurrentCoordinate(changedLocation);
                Log.d(ToolsHelper.getLogTagByClass(TrackingService.class, "OnLocationChanged"), String.format("Courier %s currentCoordinate set", courier));

                courier.requestDataFromServer(mainMapsActivity);

                Log.d(ToolsHelper.getLogTagByClass(TrackingService.class, "OnLocationChanged"),
                        String.format("Courier %s coordinates has sent (lat = %s, lng = %s)",
                                courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));*/
            } catch (Throwable ex) {
                ToolsHelper.logException(ex);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) throws SecurityException {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}

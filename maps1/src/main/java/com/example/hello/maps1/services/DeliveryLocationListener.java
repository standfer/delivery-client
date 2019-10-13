package com.example.hello.maps1.services;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.ToolsHelper;

public class DeliveryLocationListener implements LocationListener {
    private Courier courier;

    public DeliveryLocationListener(Courier courier) {
        this.courier = courier;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.i(TrackingService.class.getName(), DeliveryLocationListener.class + " service onLocationChanged");
                Coordinate changedLocation = new Coordinate(location);

                courier.setCurrentCoordinate(changedLocation);
                Log.d(ToolsHelper.getLogTagByClass(TrackingService.class, "OnLocationChanged"), String.format("Courier %s currentCoordinate set", courier));

                courier.requestDataFromServer();

                Log.d(ToolsHelper.getLogTagByClass(TrackingService.class, "OnLocationChanged"),
                        String.format("Courier %s coordinates has sent (lat = %s, lng = %s)",
                                courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}

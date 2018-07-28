package com.example.hello.maps1.asyncEngines.async_activities;

import android.os.AsyncTask;
import android.util.Pair;

import com.example.hello.maps1.OrderDetailsActivity;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.requestEngines.RouteHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;

public class OrderDetailsRouteBuilder extends AsyncTask<OrderDetailsActivity, Void, Void> {
    private GoogleMap map;
    private Courier courier;
    private Order order;
    private PolylineOptions polylineOptions;

    @Override
    protected Void doInBackground(OrderDetailsActivity... orderDetailsActivities) {
        OrderDetailsActivity activity = orderDetailsActivities[0];
        map = activity.getMap();
        courier = activity.getCourier();
        order = activity.getOrder();

        polylineOptions = RouteHelper.getRoutePolyline(courier.getCurrentCoordinate(), order.getLocation());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        map.addPolyline(polylineOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()), 10));

        RouteHelper.addMarkersByCoordinates(map, Arrays.asList(
                new Pair<>(courier.getCurrentCoordinate(), "Вы здесь"),
                new Pair<>(order.getLocation(), String.format("Доставить по адресу:%s", order.getAddress()))));
    }
}

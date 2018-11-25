package com.example.hello.maps1;

import android.app.AlarmManager;
import android.location.LocationManager;
import android.os.AsyncTask;


import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.responses.Infos;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.entities.responses.OrdersResponse;
import com.example.hello.maps1.helpers.NotificationHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.example.hello.maps1.requestEngines.RouteHelper;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Lyubov_Kutergina on 31.01.2016. // not used now, instead of - MyIntentService
 */
public class NetworkDAO extends AsyncTask<MainMapsActivity, Void, Void> {//<String, Void, String> {

    public String answerOldBridge = "", answerNewBridge = "", routeToDestination = "";
    private String answerLocalhost = "";
    private MainMapsActivity mainMapsActivity;
    private AlarmManager am;
    private TimeNotification timeNotification;
    private LocationManager locationManager;
    private Toast toast;
    private List<Coordinate> routePoints;

    private List<Order> ordersUnassigned;

    private String responseCourierData = "";

    private Courier courier;

    public NetworkDAO() {
        //locationManager =(LocationManager) mainMapsActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public NetworkDAO(MainMapsActivity mma) {
    }

    @Override
    protected Void doInBackground(MainMapsActivity... mainMapsActivities) {
        try {
            this.mainMapsActivity = mainMapsActivities[0];
            this.courier = this.mainMapsActivity.getCourier();

            Long timeDifference = System.currentTimeMillis();
            //responseCourierData = RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "action=setCourierCoords&courier=" + RequestHelper.convertObjectToJson(shortCourier));
            responseCourierData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                    String.format("action=%s&courier=%s", Constants.METHOD_NAME_updateCourierLocation, RequestHelper.convertObjectToJson(courier)));//"courier=" + RequestHelper.convertObjectToJson(courier));
            ordersUnassigned = getOrdersUnassigned(RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=getOrdersUnassigned"));

            timeDifference = System.currentTimeMillis() - timeDifference;
            Log.d("Time4Request", timeDifference.toString());
            courier.updateData(responseCourierData, ordersUnassigned); // todo add here check if a driver wants to apply new order

            if (courier.getCurrentCoordinate() != null) {
                routeToDestination = RequestHelper.requestRouteByCoordinates(courier.getCurrentCoordinate(), courier.getDestinationCoordinate(), courier.getOrders());
            } else {
                Log.d("Request", String.format(Constants.ERROR_NO_DATA_FOR_COURIER, courier.getId()));
            }
        } catch (Throwable e) {
            Log.d("Request Error", e.getStackTrace().toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        List<Coordinate> routePoints = MyIntentService.pointsCoordinates(routeToDestination);

        toast = Toast.makeText(mainMapsActivity.getApplicationContext(), "", Toast.LENGTH_LONG);
        changeMapsGuiData(this.mainMapsActivity, routePoints);

        courier.tryToAssignAvailableOrders(mainMapsActivity);

        mainMapsActivity.updateGui(courier);

        //showNotification(messageRoute);
    }

    private void changeMapsGuiData(MainMapsActivity mainMapsActivity, List<Coordinate> routePoints) {
        mainMapsActivity.getmMap().clear();
        //mainMapsActivity.setMrkCurrentPos(messageRoute);
        mainMapsActivity.getMrkCurrentPos().showInfoWindow();

        PolylineOptions routeLine = RouteHelper.getRoutePolyline(routePoints);
        mainMapsActivity.getmMap().addPolyline(routeLine);
        mainMapsActivity.setRoutePoints(routePoints);

        LatLng initPosition = new LatLng(routePoints.get(0).getLat(), routePoints.get(0).getLng());
        //LatLng destPosition = new LatLng(routePoints.get(routePoints.size() - 1).getLat(), routePoints.get(routePoints.size() - 1).getLng());

        mainMapsActivity.getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 10));
        mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(initPosition).title("Вы здесь").draggable(false));

        addWaypointMarkers(courier.getOrders(), mainMapsActivity.getmMap());

        try {
            toast.setText(courier.getDistanceToChangedLocation(courier.getDestinationCoordinate()));
            toast.show();
            //проверяем расстояние между текущими координатами местом назначения, чтобы снять заказ
            if (!courier.getOrder().isDelivered() && courier.isDestinationReached() /* || mainMapsActivity.getLocationManager().addProximityAlert();*/) {
                courier.getOrder().setDelivered(true);
                NotificationHelper.showNotification(mainMapsActivity, Constants.MSG_ORDER_TITLE, Constants.MSG_ORDER_IS_DELIVERED);
            }
        } catch(Throwable ex) { }
    }

    private void addWaypointMarkers(List<Order> orders, GoogleMap map) {
        int i = 0;
        for(Order order : orders) {
            LatLng orderPosition = new LatLng(order.getLocation().getLat(), order.getLocation().getLng());
            String orderSnippet = String.format(
                    "Адрес: %s\n" +
                    "Телефон: %s\n" +
                    "Стоимость: %s\n", order.getAddress(), order.getPhoneNumber(), order.getCost()); //responseCourierData);

            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(orderPosition).title("Доставить по адресу:" + i).snippet(orderSnippet).draggable(false));
            i++;
        }
    }

    private void addWaypointMarkersWithWorkplaces(List<Order> orders, GoogleMap map) {
        int i = 0;
        for(Order order : orders) {
            LatLng workPlacePosition = new LatLng(order.getWorkPlace().getLocation().getLat(), order.getWorkPlace().getLocation().getLng());
            LatLng orderPosition = new LatLng(order.getLocation().getLat(), order.getLocation().getLng());
            String workPlaceSnippet = String.format("Адрес: %s", order.getWorkPlace().getAddress());
            String orderSnippet = String.format(
                    "Адрес: %s\n" +
                    "Телефон: %s\n" +
                    "Стоимость: %s\n", order.getAddress(), order.getPhoneNumber(), order.getCost()); //responseCourierData);
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(workPlacePosition).title("Забрать из:" + i).snippet(workPlaceSnippet).draggable(false));
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(orderPosition).title("Доставить по адресу:" + i).snippet(orderSnippet).draggable(false));
            i++;
        }
    }

    private List<Order> getOrdersUnassigned(String responseOrders) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            OrdersResponse ordersResponse = objectMapper.readValue(responseOrders, OrdersResponse.class);

            return ordersResponse.getOrders();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void onetimeTimer(Context context) {
        if (timeNotification != null) {
            timeNotification.setOnetimeTimer(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartNotify() {
        am = (AlarmManager) mainMapsActivity.getSystemService(Context.ALARM_SERVICE);

    }
}
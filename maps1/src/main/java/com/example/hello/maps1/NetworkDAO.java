package com.example.hello.maps1;

import android.app.AlarmManager;
import android.content.Context;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.entities.responses.OrdersResponse;
import com.example.hello.maps1.helpers.JSONHelper;
import com.example.hello.maps1.helpers.NotificationHelper;
import com.example.hello.maps1.helpers.StringHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.example.hello.maps1.requestEngines.RouteHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Lyubov_Kutergina on 31.01.2016. // not used now, instead of - MyIntentService
 */
public class NetworkDAO extends AsyncTask<MainMapsActivity, Void, Courier> {//<String, Void, String> {
    protected static final String TAG = NetworkDAO.class.getName();

    public String answerOldBridge = "", answerNewBridge = "", responseRouteData = "";
    private String answerLocalhost = "";
    private MainMapsActivity mainMapsActivity;
    private AlarmManager am;
    private TimeNotification timeNotification;
    private LocationManager locationManager;
    private Toast toast;
    private List<Coordinate> routePoints;

    private List<Order> ordersUnassigned;

    private String responseCourierData = "", responseOrdersUnassignedData = "";

    private Courier courierMain;

    public NetworkDAO() {
        //locationManager =(LocationManager) mainMapsActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public NetworkDAO(MainMapsActivity mma) {
    }

    @Override
    protected Courier doInBackground(MainMapsActivity... mainMapsActivities) {
        Courier courier = null;
        try {
            this.mainMapsActivity = mainMapsActivities[0];
            courierMain = this.mainMapsActivity.getCourier();
            courier = (Courier) courierMain.clone();
            //this.courier = this.mainMapsActivity.getCourier();

            courier.setOrders(null);
            //String jsonCourierOld = RequestHelper.convertObjectToJson(courier);
            String jsonCourier = JSONHelper.getJsonFromObject(courier);

            responseCourierData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                    String.format("action=%s&courier=%s", Constants.METHOD_NAME_updateCourierLocation, jsonCourier));
            /*responseOrdersUnassignedData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                    String.format("action=%s&courier=%s", Constants.METHOD_NAME_getOrdersUnassigned, jsonCourier));*/

            courier.updateData(responseCourierData, responseOrdersUnassignedData);

            /*if (courier.getCurrentCoordinate() != null) {
                responseRouteData = RequestHelper.requestRouteByCoordinates(courier.getCurrentCoordinate(), courier.getDestinationCoordinate(), courier.getOrders());
                Log.d(ToolsHelper.getLogTagByClass(NetworkDAO.class, "doInBackground"),
                        String.format("Route loaded successfully by coordinates: %s", responseCourierData));
            } else {
                Log.e("Request", String.format(Constants.ERROR_NO_DATA_FOR_COURIER, courier.getId()));
            }*/
        } catch (Throwable e) {
            Log.d(TAG, e.getStackTrace().toString());
        }

        /*List<Order> testOrders = new ArrayList<>();
        courier.setCurrentCoordinate(new Coordinate(53, 50));
        testOrders.add(new Order());
        courier.setOrders(testOrders);*/

        /*List<Order> testOrders = new ArrayList<>();

        for (Order order : courier.getOrdersAvailable()) {
            testOrders.add(order);
        }*/

        return courier;
    }

    @Override
    protected void onPostExecute(Courier courier) {
        ToolsHelper.logOrders(NetworkDAO.class, "onPostExecute", courier.getOrders(), courier.getOrdersAvailable());
        super.onPostExecute(courier);
        this.mainMapsActivity.setCourier(courier);
        try {
            //courier.tryToAssignAvailableOrders(mainMapsActivity);
            if (!StringHelper.isEmpty(responseRouteData)) {
                List<Coordinate> routePoints = MyIntentService.pointsCoordinates(responseRouteData);

                updateMapGui(this.mainMapsActivity, courier, routePoints);
                mainMapsActivity.updateGui(courier);
                makeOrderIsDelivered(courier);
            }
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    private void updateMapGui(MainMapsActivity mainMapsActivity, Courier courier, List<Coordinate> routePoints) {
        try {
            if (mainMapsActivity.getmMap() == null) return;

            mainMapsActivity.getmMap().clear();
            //mainMapsActivity.setMrkCurrentPos(messageRoute);
            mainMapsActivity.getMrkCurrentPos().showInfoWindow();

            PolylineOptions routeLine = RouteHelper.getRoutePolyline(routePoints);
            mainMapsActivity.getmMap().addPolyline(routeLine);
            mainMapsActivity.setRoutePoints(routePoints);

            LatLng initPosition = new LatLng(routePoints.get(0).getLat(), routePoints.get(0).getLng());
            //LatLng destPosition = new LatLng(routePoints.get(routePoints.size() - 1).getLat(), routePoints.get(routePoints.size() - 1).getLng());

            //mainMapsActivity.getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 10));
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(initPosition).title("Вы здесь").draggable(false));

            addWaypointMarkers(courier.getOrders(), mainMapsActivity.getmMap());
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    private void makeOrderIsDelivered(Courier courier) {
        try {
            //toast.setText(courier.getDistanceToChangedLocation(courier.getDestinationCoordinate()));
            //toast.show();
            //проверяем расстояние между текущими координатами местом назначения, чтобы снять заказ
            if (/*!courier.getOrder().isDelivered() &&*/ courier.isAnyOrderLocationReached() /* || mainMapsActivity.getLocationManager().addProximityAlert();*/) {
                courier.getOrder().setDelivered(true);
                NotificationHelper.showNotification(mainMapsActivity, Constants.MSG_ORDER_TITLE, Constants.MSG_ORDER_IS_DELIVERED);
            }
        } catch (Throwable ex) {
            Log.d(TAG, "IsDelivered failed, ex: " + ex.toString());
        }
    }

    private void addWaypointMarkers(List<Order> orders, GoogleMap map) {
        int i = 0;
        for (Order order : orders) {
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
        for (Order order : orders) {
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
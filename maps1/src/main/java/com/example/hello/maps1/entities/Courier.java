package com.example.hello.maps1.entities;

import android.app.AlertDialog;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.NetworkDAO;
import com.example.hello.maps1.activities.alerts.AlertDialogFragment;
import com.example.hello.maps1.asyncEngines.OrdersAssignment;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.helpers.CollectionsHelper;
import com.example.hello.maps1.helpers.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ivan_grinenko on 23.08.2016.
 */
public class Courier implements Serializable {
    private int id;
    private String name;
    private String surName;
    private Long phone;
    private int idWorkPlace;

    private String login;
    private String password;

    //for server test
    private double geo_lat;
    private double geo_lon;
    private String serverResponse;

    private Coordinate currentCoordinate;
    private Coordinate destinationCoordinate;
    private Coordinate manualCurrentCoordinate;

    private Order order;
    private List<Order> orders;
    private List<Order> ordersAvailable;
    private List<Order> ordersToAssign;

    private String data;

    private double proximityAlertRadius = 300;

    public Courier() {
    }

    public Courier(int id) {
        this.id = id;
    }

    public Courier(int id, String name, int idWorkPlace) {
        this.id = id;
        this.name = name;
        this.idWorkPlace = idWorkPlace;
        this.orders = new ArrayList<>();
    }

    public Courier(int id, String name, int idWorkPlace, String address, String phoneNumber, double cost, boolean isDelivered) {
        this.id = id;
        this.name = name;
        this.idWorkPlace = idWorkPlace;
        this.order = new Order(address, phoneNumber, cost, isDelivered);
        this.orders = new ArrayList<>();
    }

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdWorkPlace() {
        return idWorkPlace;
    }

    public void setIdWorkPlace(int idWorkPlace) {
        this.idWorkPlace = idWorkPlace;
    }

    public Coordinate getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setCurrentCoordinate(Coordinate currentCoordinate) {
        this.currentCoordinate = currentCoordinate;
        this.geo_lat = currentCoordinate.getLat();
        this.geo_lon = currentCoordinate.getLng();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        //this.serverResponse = data; // for test
        //initFieldsFromJson(data);
        initOrdersFromJsonArray(data);
    }

    public Coordinate getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public void setDestinationCoordinate(Coordinate destinationCoordinate) {
        this.destinationCoordinate = destinationCoordinate;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Coordinate getManualCurrentCoordinate() {
        return manualCurrentCoordinate;
    }

    public void setManualCurrentCoordinate(Coordinate manualCurrentCoordinate) {
        this.manualCurrentCoordinate = manualCurrentCoordinate;
    }

    public double getLat() {
        return this.currentCoordinate.getLat();
    }

    public void setLat(double lat) {
        this.geo_lat = lat;
        this.currentCoordinate.setLat(lat);
    }

    public double getLng() {
        return this.currentCoordinate.getLat();
    }

    public void setLng(double lng) {
        this.geo_lon = lng;
        this.currentCoordinate.setLng(lng);
    }

    public String getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void clear() {
        this.data = "";
        this.order = null;
        if(this.orders != null) this.orders.clear();

        if (!CollectionsHelper.isEmpty(this.ordersToAssign)) {
            this.ordersToAssign.clear();
        }
        if (!CollectionsHelper.isEmpty(this.ordersAvailable)) {
            this.ordersAvailable.clear();
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getOrdersAvailable() {
        return ordersAvailable;
    }

    public void setOrdersAvailable(List<Order> ordersAvailable) {
        this.ordersAvailable = ordersAvailable;
    }

    public void updateData(String responseOrdersAssigned, List<Order> ordersUnassigned) {
        initOrdersFromJsonArray(responseOrdersAssigned);
        this.ordersAvailable = findOrdersToAssign(ordersUnassigned);
    }

    private void initFieldsFromJson(String data) {
        try {
            if (true || data.isEmpty() || data.contains("status")) {
                data = String.format("{\"Address\":\"Буровик\",\"Phone\":\"88463521098\",\"Cost\":1208,\"Lat\":53.1157204,\"Lng\":50.0847883}");
                this.data = data;
            }
            JSONObject orderDetails = new JSONObject(data);
            this.order = new Order(orderDetails.getString("Address"), orderDetails.getString("Phone"), orderDetails.getDouble("Cost"),
                    this.order != null ? this.order.isDelivered() : false);
            this.destinationCoordinate = new Coordinate(orderDetails.getDouble("Lat"), orderDetails.getDouble("Lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initOrdersFromJsonArray(String data) {
        try {
            JSONObject info = new JSONObject(data);
            JSONArray orders = info.getJSONArray("infos"); //jsonObject.getJSONArray("...");
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = new JSONObject(orders.getString(i));
                this.orders.add(new Order(
                        order.getInt("idOrder"),
                        order.getString("address"),
                        order.getDouble("lat"),
                        order.getDouble("lng"),
                        order.getString("phoneNumber"),
                        order.getDouble("cost"),
                        order.getInt("isDelivered"),
                        order.getInt("idWorkplace"),
                        order.getString("addressWorkplace"),
                        order.getDouble("latWorkPlace"),
                        order.getDouble("lngWorkPlace"),
                        order.getInt("priority"),
                        order.getDouble("odd"),
                        order.getString("notes"),
                        order.getInt("idClient"),
                        order.getString("clientName"),
                        order.getString("clientPhone")
                ));
            }
            if(this.orders != null && !this.orders.isEmpty() && this.orders.get(0) != null) {
                Collections.sort(this.orders);
                this.destinationCoordinate = new Coordinate(this.orders.get(this.orders.size() - 1).getLocation().getLat(),
                        this.orders.get(this.orders.size() - 1).getLocation().getLng());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initCouriersFromJsonArray(String data) {
        try {
            //JSONObject jsonObject = new JSONObject(data);

            JSONArray couriers = new JSONArray(data); //jsonObject.getJSONArray("");
            for (int i = 0; i < couriers.length(); i++) {
                JSONObject courier = new JSONObject(couriers.getString(0));
                if (courier.getString("Name").equals(this.name)) {
                    this.name = courier.getString("Name");
                    JSONArray destCoordinates = courier.getJSONArray("Coords");
                    JSONObject destCoordinate = new JSONObject(destCoordinates.getString(0));
                    this.destinationCoordinate = new Coordinate(destCoordinate.getDouble("lat"), destCoordinate.getDouble("lng"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestDataFromServer(MainMapsActivity mainMapsActivity) {
        if (isReadyToRequest()) {
            NetworkDAO networkDAO = new NetworkDAO();
            try {
                //timeNotification = new TimeNotification(this, null);
                //timeNotification.setOnetimeTimer(this.getApplicationContext());

                //onetimeTimer();
                networkDAO.execute(mainMapsActivity);
                //isRouteNeed = false;
                //getRequest = networkDAO.request(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDestinationReached() {
        return currentCoordinate != null && destinationCoordinate != null ?
                currentCoordinate.distanceTo(destinationCoordinate.getLat(), destinationCoordinate.getLng()) < proximityAlertRadius : false;
    }

    public boolean isRebuildRouteNeeded(Coordinate changedLocation) {
        return currentCoordinate != null && changedLocation != null ?
                currentCoordinate.distanceTo(changedLocation.getLat(), changedLocation.getLng()) > 50 : false;
    }

    public String getDistanceToChangedLocation(Coordinate changedLocation) {
        return currentCoordinate != null && changedLocation != null ?
                "ChangedDistance: " + currentCoordinate.distanceTo(changedLocation.getLat(), changedLocation.getLng()) : "null";
    }

    public boolean isReadyToRequest() {
        return this != null && this.getId() != 0;
    }

    public List<Order> findOrdersToAssign(List<Order> ordersUnassigned) {
        List<Order> orders = new ArrayList<>();

        for (Order order : ordersUnassigned) {
            if (isDriverCanTakeOrder(order)) {
                orders.add(order);
            }
        }
        return orders;
    }

    public boolean isDriverCanTakeOrder(Order order) {
        return !Coordinate.isEmpty(currentCoordinate) && order != null &&
                order.getWorkPlace() != null && !Coordinate.isEmpty(order.getWorkPlace().getLocation()) && true;
                //currentCoordinate.distanceTo(order.getWorkPlace().getLocation()) <= Constants.DISTANCE_DRIVER_NEAR_WORKPLACE;
    }

    public void tryToAssignAvailableOrders(MainMapsActivity mainMapsActivity) {
        if (!CollectionsHelper.isEmpty(this.ordersAvailable)) {

            AlertDialog ordersToAssignDialog = AlertDialogFragment.showOrdersToAssign(mainMapsActivity);
            NotificationHelper.showNotification(mainMapsActivity, Constants.MSG_ORDERS_AVAILABLE_TITLE, Constants.MSG_ORDERS_AVAILABLE);
            ordersToAssignDialog.show();
        }
    }

    public void updateOrdersAssignedInDb(MainMapsActivity mainMapsActivity) {
        if (!CollectionsHelper.isEmpty(ordersToAssign)) {
            OrdersAssignment ordersAssignment = new OrdersAssignment();
            ordersAssignment.execute(mainMapsActivity);
        }
    }

    public List<String> getOrdersData(List<Order> orders) {
        List<String> ordersData = new ArrayList<>();

        for (Order order : orders) {
            ordersData.add(order.getData());
        }

        return ordersData;
    }

    public String[] getOrdersMassive(List<Order> orders) {
        String[] ordersMassive = new String[orders.size()];
        orders.toArray(ordersMassive);

        return ordersMassive;
    }

    public List<Order> getOrdersToAssign() {
        return ordersToAssign != null ? ordersToAssign : new ArrayList<Order>();
    }

    public void setOrdersToAssign(List<Order> ordersToAssign) {
        this.ordersToAssign = ordersToAssign;
    }

    public String getSurname() {
        return surName;
    }

    public void setSurname(String surName) {
        this.surName = surName;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
}

package com.example.hello.maps1.entities;

import android.app.AlertDialog;
import android.location.Location;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.NetworkDAO;
import com.example.hello.maps1.activities.alerts.AlertDialogFragment;
import com.example.hello.maps1.asyncEngines.LocationUpdater;
import com.example.hello.maps1.asyncEngines.OrdersAssignment;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.responses.Infos;
import com.example.hello.maps1.helpers.CollectionsHelper;
import com.example.hello.maps1.helpers.DateTimeHelper;
import com.example.hello.maps1.helpers.JSONHelper;
import com.example.hello.maps1.helpers.NotificationHelper;
import com.example.hello.maps1.helpers.StringHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ivan_grinenko on 23.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Courier
        extends BaseEntity
        implements Serializable, Cloneable {
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

    @JsonProperty("orders")
    private List<Order> orders;

    private List<Order> ordersAvailable;
    private List<Order> ordersToAssign;

    private boolean isReadyToAssign = true;
    private DateTime assignmentDate = new DateTime(); //todo check if clone override need

    private String data;

    public Courier() {
    }

    public Courier(int id) {
        setId(id);
    }

    public Courier(int id, String name, int idWorkPlace) {
        setId(id);
        this.name = name;
        this.idWorkPlace = idWorkPlace;
        this.orders = new ArrayList<>();
    }

    public Courier(int id, String name, int idWorkPlace, String address, String phoneNumber, double cost, boolean isDelivered) {
        setId(id);
        this.name = name;
        this.idWorkPlace = idWorkPlace;
        this.order = new Order(address, phoneNumber, cost, isDelivered);
        this.orders = new ArrayList<>();
    }

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
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

    public void setCurrentCoordinate(Location location) {
        setCurrentCoordinate(new Coordinate(location));
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Courier courierCloned = (Courier) super.clone();

        if (currentCoordinate != null) courierCloned.setCurrentCoordinate((Coordinate) currentCoordinate.clone());
        if (destinationCoordinate != null) courierCloned.setDestinationCoordinate((Coordinate) destinationCoordinate.clone());
        if (manualCurrentCoordinate != null) courierCloned.setManualCurrentCoordinate((Coordinate) manualCurrentCoordinate.clone());

        if (order != null) courierCloned.setOrder((Order) order.clone());

        courierCloned.setOrders((List<Order>) CollectionsHelper.getCollectionCloned(orders));
        courierCloned.setOrdersAvailable((List<Order>) CollectionsHelper.getCollectionCloned(ordersAvailable));
        courierCloned.setOrdersToAssign((List<Order>) CollectionsHelper.getCollectionCloned(ordersToAssign));

        return courierCloned;
    }

    public void clear() {
        this.data = "";
        this.order = null;
        if (this.orders != null) this.orders.clear();

        if (!CollectionsHelper.isEmpty(this.ordersToAssign)) {
            this.ordersToAssign.clear();
        }
        if (!CollectionsHelper.isEmpty(this.ordersAvailable)) {
            this.ordersAvailable.clear();
        }

        Log.d(String.valueOf(Courier.class), String.format("Courier %s cleared", this));
    }

    public void clearAssignment() {
        /*if (!CollectionsHelper.isEmpty(this.ordersToAssign)) {
            this.ordersToAssign.clear();
        }
        if (!CollectionsHelper.isEmpty(this.ordersAvailable)) {
            this.ordersAvailable.clear();
        }

        Log.d(String.valueOf(Courier.class), String.format("Courier %s assignment cleared", this));*/
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

    public void updateData(String responseOrders, String responseOrdersUnassigned) {
        try {
            Infos infosAssigned = (Infos) JSONHelper.getObjectFromJson(responseOrders, Infos.class);

            if (infosAssigned != null && !infosAssigned.isEmpty()) {
                this.orders = infosAssigned.getOrders();
                if (this.orders != null && !this.orders.isEmpty() && this.orders.get(0) != null) {
                    Collections.sort(this.orders);
                    this.destinationCoordinate = new Coordinate(this.orders.get(this.orders.size() - 1).getLocation().getLat(),
                            this.orders.get(this.orders.size() - 1).getLocation().getLng());
                }
            }

            Log.d("Courier", String.format("Update courier.orders (%s)", !CollectionsHelper.isEmpty(orders) ? orders.toString() + ":" + orders.size() : null));

            if (!StringHelper.isEmpty(responseOrdersUnassigned)) {
                Infos infosUnassigned = (Infos) JSONHelper.getObjectFromJson(responseOrdersUnassigned, Infos.class);

                if (infosUnassigned != null && !infosUnassigned.isEmpty()) {
                    this.ordersAvailable = infosUnassigned.getOrders();
                    //if (ordersAvailable == null) ordersAvailable = new ArrayList<>();
                /*Order testOrder = new Order();
                testOrder.setWorkPlace(new WorkPlace(1, "address workplace", 53.0001, 50.0006));
                this.ordersAvailable.add(testOrder);*/

                    checkOrdersAvailable(this.ordersAvailable);
                }

                Log.d("Courier", String.format("Update courier.ordersAvailable (%s)", !CollectionsHelper.isEmpty(ordersAvailable) ? ordersAvailable.toString() + ":" + ordersAvailable.size() : null));
            }
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    /*public void updateData(String responseOrdersAssigned, List<Order> ordersUnassigned) {
        //initOrdersFromJsonArray(responseOrdersAssigned);
        Infos infos = (Infos) JSONHelper.getObjectFromJson(responseOrdersAssigned, Infos.class);

        if (infos != null) {
            this.orders = infos.getOrders();
        }

        this.ordersAvailable = checkOrdersAvailable(ordersUnassigned);
    }*/

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
            if (this.orders != null && !this.orders.isEmpty() && this.orders.get(0) != null) {
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

    public void requestDataFromServer() {
        if (isReadyToRequest()) {
            LocationUpdater locationUpdater = new LocationUpdater();
            try {
                locationUpdater.execute(this);
            } catch (Exception e) {
                ToolsHelper.logException(e);
            }
        }
    }

    public boolean isDestinationReached() {
        return currentCoordinate != null && destinationCoordinate != null ?
                currentCoordinate.distanceTo(destinationCoordinate.getLat(), destinationCoordinate.getLng()) < Constants.PROXIMITY_ALERT_RADIUS : false;
    }

    public boolean isAnyOrderLocationReached() {
        if (CollectionsHelper.isEmpty(orders)) return false;

        for (Order order :orders) {
            Coordinate orderLocation = order.getLocation();
            if (currentCoordinate != null &&
                    currentCoordinate.distanceTo(orderLocation.getLat(), orderLocation.getLng()) < Constants.PROXIMITY_ALERT_RADIUS) {
                return true;
            }
        }

        return false;
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

    public void checkOrdersAvailable(List<Order> ordersUnassigned) {
        if (CollectionsHelper.isEmpty(ordersUnassigned)) return;

        Iterator<Order> iterator = ordersUnassigned.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (!isLocationsDefined(order)) {
                ordersUnassigned.remove(order);
            }
        }
    }

    public boolean isLocationsDefined(Order order) {
        return !Coordinate.isEmpty(currentCoordinate) && order != null &&
                !WorkPlace.isEmpty(order.getWorkPlace());
        //currentCoordinate.distanceTo(order.getWorkPlace().getLocation()) <= Constants.DISTANCE_DRIVER_NEAR_WORKPLACE;
    }

    public void tryToAssignAvailableOrders(MainMapsActivity mainMapsActivity) throws CloneNotSupportedException {
        if (!CollectionsHelper.isEmpty(this.ordersAvailable)) {

            AlertDialog ordersToAssignDialog = AlertDialogFragment.showOrdersToAssign(mainMapsActivity);

            if (ordersToAssignDialog != null) {
                NotificationHelper.showNotification(mainMapsActivity, Constants.MSG_ORDERS_AVAILABLE_TITLE, Constants.MSG_ORDERS_AVAILABLE);
                ordersToAssignDialog.show();
                mainMapsActivity.setTimerActive(false);
            }
        }
    }

    public boolean isReadyToAssign() {
        return isReadyToAssign && DateTimeHelper.isTimeoutPassed(assignmentDate, Constants.TIMEOUT_COURIER_ASSIGNMENT);
    }

    public void updateAssignmentState() {
        setAssignmentDate(new DateTime());
        setReadyToAssign(true);
    }

    public synchronized void assignOrders(List<Order> ordersToAssign, MainMapsActivity mainMapsActivity) {
        this.ordersToAssign = ordersToAssign;
        updateOrdersAssignedInDb(mainMapsActivity);
    }

    private void updateOrdersAssignedInDb(MainMapsActivity mainMapsActivity) {
        if (!CollectionsHelper.isEmpty(ordersToAssign)) {
            OrdersAssignment ordersAssignment = new OrdersAssignment();
            mainMapsActivity.setCourier(this);
            ordersAssignment.execute(mainMapsActivity);
        }
    }

    public List<String> getOrdersData(List<Order> orders) {
        List<String> ordersData = new ArrayList<>();

        for (Order order : orders) {
            ordersData.add(order.toString());
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

    public DateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(DateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public void setReadyToAssign(boolean readyToAssign) {
        isReadyToAssign = readyToAssign;
    }
}

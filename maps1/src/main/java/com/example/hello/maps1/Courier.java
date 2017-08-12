package com.example.hello.maps1;

import com.example.hello.maps1.entities.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivan_grinenko on 23.08.2016.
 */
public class Courier {
    private int id;
    private String name;
    private int idWorkPlace;

    //for server test
    private double geo_lat;
    private double geo_lon;
    private String serverResponse;

    private Coordinate currentCoordinate;
    private Coordinate destinationCoordinate;
    private Coordinate manualCurrentCoordinate;

    private Order order;
    private List<Order> orders;
    private String data;


    private double proximityAlertRadius = 300;


    public Courier() {
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


    public void requestDataFromServer(MainMapsActivity mainMapsActivity) {
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

    public void sendDataToServer() {

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
            JSONArray orders = info.getJSONArray("info"); //jsonObject.getJSONArray("...");
            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = new JSONObject(orders.getString(i));
                this.orders.add(new Order(
                        order.getString("address"),
                        order.getDouble("lat"),
                        order.getDouble("lng"),
                        order.getString("phoneNumber"),
                        order.getDouble("cost"),
                        order.getInt("isDelivered"),
                        order.getInt("idWorkplace"),
                        order.getString("addressWorkplace"),
                        order.getDouble("latWorkPlace"),
                        order.getDouble("lngWorkPlace")
                ));
            }
            if(this.orders != null && this.orders.get(0) != null) {
                this.destinationCoordinate = new Coordinate(this.orders.get(this.orders.size() - 1).getAddressCoordinate().getLat(),
                        this.orders.get(this.orders.size() - 1).getAddressCoordinate().getLng());
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
    }

}

package com.example.hello.maps1.entities;

/**
 * Created by Ivan on 09.04.2017.
 */

public class Courier { // for test ilkato, not need

    private int id;
    private double geo_lat;
    private double geo_lon;
    private double lat;
    private double lng;

    public Courier(int id, double geo_lat, double geo_lon) {
        this.id = id;
        this.geo_lat = geo_lat;
        this.geo_lon = geo_lon;
        this.lat = geo_lat;
        this.lng = geo_lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getGeo_lat() {
        return geo_lat;
    }

    public void setGeo_lat(double geo_lat) {
        this.geo_lat = geo_lat;
    }

    public double getGeo_lon() {
        return geo_lon;
    }

    public void setGeo_lon(double geo_lon) {
        this.geo_lon = geo_lon;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

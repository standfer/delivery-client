package com.example.hello.maps1.entities;

import android.location.Location;
import android.location.LocationManager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by ivan_grinenko on 14.08.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coordinate extends BaseEntity
        implements Serializable, Cloneable {

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lng")
    private Double lng;

    public Coordinate() {
    }

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Coordinate(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String toString() {
        return String.format("%s,%s", lat, lng);
    }

    public double distanceTo(Coordinate destLocation) {
        return !Coordinate.isEmpty(destLocation) ? distanceTo(destLocation.getLat(), destLocation.getLng()) : null; //todo check if null for double is ok, had not to make Double
    }

    public double distanceTo(double destLat, double destLng) {//расстояние между координатами для проверки, нужно ли перестраивать маршрут

        Location origin = new Location(LocationManager.GPS_PROVIDER);
        Location dest = new Location(LocationManager.GPS_PROVIDER);
        origin.setLatitude(lat);
        origin.setLongitude(lng);
        dest.setLatitude(destLat);
        dest.setLongitude(destLng);

        return origin.distanceTo(dest);
    }

    public String toJson() {//сериализация объекта координата
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    public static boolean isEmpty(Coordinate coordinate) {
        return coordinate == null || coordinate.getLat() == null || coordinate.getLng() == null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

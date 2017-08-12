package com.example.hello.maps1;

import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

/**
 * Created by ivan_grinenko on 14.08.2016.
 */
public class Coordinate {

    private double lat;
    private double lng;
    public Coordinate(){
    }
    public Coordinate(double lat, double lng){
        this.lat=lat;
        this.lng=lng;
    }
    public Coordinate(Location location){
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }
    public double distanceTo(double destLat, double destLng){//расстояние между координатами для проверки, нужно ли перестраивать маршрут

        Location origin = new Location(LocationManager.GPS_PROVIDER);
        Location dest = new Location(LocationManager.GPS_PROVIDER);
        origin.setLatitude(lat); origin.setLongitude(lng);
        dest.setLatitude(destLat); dest.setLongitude(destLng);

        return origin.distanceTo(dest);
    }

    public String toJson(){//сериализация объекта координата
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        catch(Exception ex){
            return ex.toString();
        }
    }

    public String toString(){
        return String.format("%s,%s", lat, lng);
    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}

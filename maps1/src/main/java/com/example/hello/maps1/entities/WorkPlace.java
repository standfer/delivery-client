package com.example.hello.maps1.entities;

import com.example.hello.maps1.Coordinate;

/**
 * Created by Ivan on 14.05.2017.
 */

public class WorkPlace {
    private int id;
    private String address;
    private Coordinate location;

    public WorkPlace(){}

    public WorkPlace(int id, String address, double latitude, double longitude) {
        this.id = id;
        this.address = address;
        this.location = new Coordinate(latitude, longitude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }
}

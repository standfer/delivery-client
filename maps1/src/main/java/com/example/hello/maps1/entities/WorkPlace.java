package com.example.hello.maps1.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Ivan on 14.05.2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkPlace extends BaseEntity
        implements Serializable, Cloneable {
    private String address;
    private Coordinate location;

    public WorkPlace(){}

    public WorkPlace(int id, String address, double latitude, double longitude) {
        setId(id);
        this.address = address;
        this.location = new Coordinate(latitude, longitude);
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

    public static boolean isEmpty(WorkPlace workPlace) {
        return workPlace == null || Coordinate.isEmpty(workPlace.getLocation());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        WorkPlace workPlaceCloned = (WorkPlace) super.clone();

        if (location != null) {
            workPlaceCloned.setLocation(new Coordinate(location.getLat(), location.getLng()));
        }

        return workPlaceCloned;
    }
}

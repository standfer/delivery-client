package com.example.hello.maps1.entities;

import com.fasterxml.jackson.databind.ser.Serializers;

import java.io.Serializable;

/**
 * Created by Ivan on 02.06.2018.
 */

public class BaseEntity implements Serializable {
    private int id;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

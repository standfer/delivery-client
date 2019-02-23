package com.example.hello.maps1.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Ivan on 04.11.2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product extends BaseEntity
        implements Serializable, Cloneable {

    private String name;
    private Double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

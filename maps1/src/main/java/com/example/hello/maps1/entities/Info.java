package com.example.hello.maps1.entities;

import com.example.hello.maps1.entities.adapters.BooleanAdapter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Ivan on 10.12.2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Info {
    private String address;
    private Double lat;
    private Double lng;
    private String phoneNumber;
    private Double cost;

    /*@JsonProperty("isDelivered")
    @JsonDeserialize(using = BooleanAdapter.Deserializer.class)
    @JsonSerialize(using = BooleanAdapter.Serializer.class)
    private Boolean isDelivered;*/

    private Integer idWorkplace;
    private String addressWorkPlace;
    private Double latWorkPlace;
    private Double lngWorkPlace;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    /*public Boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(Boolean delivered) {
        isDelivered = delivered;
    }*/

    /*public void setDelivered(Integer delivered) {
        isDelivered = delivered != null && delivered > 0 ? true : false;
    }*/

    public Integer getIdWorkplace() {
        return idWorkplace;
    }

    public void setIdWorkplace(Integer idWorkplace) {
        this.idWorkplace = idWorkplace;
    }

    public String getAddressWorkPlace() {
        return addressWorkPlace;
    }

    public void setAddressWorkPlace(String addressWorkPlace) {
        this.addressWorkPlace = addressWorkPlace;
    }

    public Double getLatWorkPlace() {
        return latWorkPlace;
    }

    public void setLatWorkPlace(Double latWorkPlace) {
        this.latWorkPlace = latWorkPlace;
    }

    public Double getLngWorkPlace() {
        return lngWorkPlace;
    }

    public void setLngWorkPlace(Double lngWorkPlace) {
        this.lngWorkPlace = lngWorkPlace;
    }
}

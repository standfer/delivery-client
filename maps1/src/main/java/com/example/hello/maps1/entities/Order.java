package com.example.hello.maps1.entities;

//import org.joda.time.DateTime;

import com.example.hello.maps1.entities.adapters.DateTimeAdapter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.joda.time.DateTime;

/**
 * Created by Ivan on 12.03.2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private int id;

    private String address;
    private String phoneNumber;
    private double cost;

    @JsonIgnore
    private boolean isDelivered;

    @JsonProperty("create_ts")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime createTs;

    @JsonProperty("deliver_ts")
    @JsonDeserialize(using = DateTimeAdapter.Deserializer.class)
    @JsonSerialize(using = DateTimeAdapter.Serializer.class)
    private DateTime deliverTs;

    private boolean isAppotinted;

    @JsonProperty("location")
    private Coordinate location;

    @JsonProperty("workPlace")
    private WorkPlace workPlace;


    public Order(){}

    public Order(String address, String phoneNumber, double cost, boolean isDelivered) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cost = cost;
        this.isDelivered = isDelivered;
    }

    public Order(String address, double lat, double lng, String phoneNumber, double cost, int isDelivered,
                 int idWorkPlace, String addressWorkPlace, double latWorkPlace, double lngWorkPlace) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.cost = cost;
        setDelivered(isDelivered);
        this.location = new Coordinate(lat, lng);
        this.workPlace = new WorkPlace(idWorkPlace, addressWorkPlace, latWorkPlace, lngWorkPlace);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public void setDelivered(Integer delivered) {
        isDelivered = delivered != null && delivered > 0 ? true : false;
    }

    public String toString() {
        return String.format("%s\n%s\n%s\n%s\n",
                this.address,
                this.phoneNumber,
                this.cost,
                this.isDelivered);
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public WorkPlace getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(WorkPlace workPlace) {
        this.workPlace = workPlace;
    }

    public boolean isAppotinted() {
        return isAppotinted;
    }

    public void setAppotinted(boolean appotinted) {
        isAppotinted = appotinted;
    }

    public DateTime getCreateTs() {
        return createTs;
    }

    public void setCreateTs(DateTime createTs) {
        this.createTs = createTs;
    }

    public DateTime getDeliverTs() {
        return deliverTs;
    }

    public void setDeliverTs(DateTime deliverTs) {
        this.deliverTs = deliverTs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return String.format(
                "Order %s\n" +
                "Address: %s\n" +
                "Phone: %s\n" +
                "Cost: %s\n", this.getId(), this.getAddress(), this.getPhoneNumber(), this.getCost());
    }
}

package com.example.hello.maps1.entities;

//import org.joda.time.DateTime;

/**
 * Created by Ivan on 12.03.2017.
 */

public class Order {

    private String address;
    private String phoneNumber;
    private double cost;
    private boolean isDelivered;

    //private DateTime create_ts;
    //private DateTime deliver_ts;
    private boolean isAppotinted;

    private Coordinate addressCoordinate;
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
        this.addressCoordinate = new Coordinate(lat, lng);
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

    public Coordinate getAddressCoordinate() {
        return addressCoordinate;
    }

    public void setAddressCoordinate(Coordinate addressCoordinate) {
        this.addressCoordinate = addressCoordinate;
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

    /*public DateTime getCreate_ts() {
        return create_ts;
    }

    public void setCreate_ts(DateTime create_ts) {
        this.create_ts = create_ts;
    }

    public DateTime getDeliver_ts() {
        return deliver_ts;
    }

    public void setDeliver_ts(DateTime deliver_ts) {
        this.deliver_ts = deliver_ts;
    }*/
}

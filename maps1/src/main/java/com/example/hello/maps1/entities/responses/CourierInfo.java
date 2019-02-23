package com.example.hello.maps1.entities.responses;

import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.CollectionsHelper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Ivan on 17.12.2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourierInfo {
    private int id;
    private List<Order> ordersToAssign;

    public CourierInfo() {

    }

    public CourierInfo(int id, List<Order> ordersToAssign) {
        this.id = id;
        this.ordersToAssign = ordersToAssign;

        /*if (!CollectionsHelper.isEmpty(this.ordersToAssign)) {
            for (Order order : ordersToAssign) {
                if (order.getWorkPlace() != null) {
                    order.getWorkPlace().setAddress("");
                }
            }
        }*/
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Order> getOrdersToAssign() {
        return ordersToAssign;
    }

    public void setOrdersToAssign(List<Order> ordersToAssign) {
        this.ordersToAssign = ordersToAssign;
    }
}

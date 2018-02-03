package com.example.hello.maps1.entities.responses;

import com.example.hello.maps1.entities.Order;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Ivan on 10.12.2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdersResponse {
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

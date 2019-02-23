package com.example.hello.maps1.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Ivan on 04.11.2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInOrder extends BaseEntity
        implements Serializable, Cloneable {
    private Order order;
    private Product product;

    private int quantity;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ProductInOrder productInOrderCloned = (ProductInOrder) super.clone();

        if (order != null) {
            productInOrderCloned.setOrder((Order) order.clone());
        }

        if (product != null) {
            productInOrderCloned.setProduct((Product) product.clone());
        }

        return super.clone();
    }
}

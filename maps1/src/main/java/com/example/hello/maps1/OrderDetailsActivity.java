package com.example.hello.maps1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hello.maps1.asyncEngines.async_activities.OrderDetailsRouteBuilder;
import com.example.hello.maps1.asyncEngines.impl.AsyncExecutorImpl;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Client;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.entities.Route;
import com.example.hello.maps1.listeners.impl.BtnCallListenerImpl;
import com.example.hello.maps1.requestEngines.InfoWindowAdapterImpl;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.example.hello.maps1.requestEngines.RouteHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * Created by Ivan on 21.08.2017.
 */

public class OrderDetailsActivity extends FragmentActivity implements OnMapReadyCallback {

    private OrderDetailsActivity orderDetailsActivity;
    private GoogleMap map;
    private Button btnBack, btnCallClient, btnCallOperator;
    private TextView tvCourierNumber, tvClientName, tvOrderNumber, tvOddMoneyFrom, tvOrderAddressTitle, tvOrderAddress,
            tvOrderApproveTimeTitle, tvOrderApproveTime, tvOrderCost, tvOrderDeliveryTimeTitle,
            tvOrderDeliveryTime, tvOrderInfo, tvNotes;

    private Courier courier;
    private Order order;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setHasOptionsMenu(true);
        mapFragment.getMapAsync(this);

        this.orderDetailsActivity = this;


        Bundle parameters = getIntent().getExtras();
        if (parameters == null) return;

        order = (Order) parameters.getSerializable(Order.class.getSimpleName());
        client = order.getClient();
        courier = (Courier) parameters.getSerializable(Courier.class.getSimpleName());

        initGui();
        initData();
    }



    private void initData() {
        tvCourierNumber.append(courier.getId() + "");

        tvOrderNumber.setText(order.getId() + "");
        tvOddMoneyFrom.append(" " + order.getOdd());
        tvOrderAddress.setText(order.getAddress());

        tvOrderApproveTime.setText(order.getCreateTs() != null ? order.getCreateTs().toString() : "22:30");
        tvOrderDeliveryTime.setText(order.getDeliverTs() != null ? order.getDeliverTs().toString() : "23:30");

        tvOrderCost.setText(order.getCost() + "");
        tvNotes.setText(order.getNotes());


        if (client != null) {
            tvClientName.setText(client.getName());
            btnCallClient.setText(String.format("%s тел. %s", btnCallClient.getText(), client.getPhone()));
        }
    }

    private void initGui() {
        btnCallClient = (Button) findViewById(R.id.btnCallClient);
        btnCallOperator = (Button) findViewById(R.id.btnCallOperator);
        btnBack = (Button) findViewById(R.id.btnBack);

        tvCourierNumber = (TextView) findViewById(R.id.tvCourierNumber);
        tvClientName = (TextView) findViewById(R.id.tvClientName);
        tvOrderAddress = (TextView) findViewById(R.id.tvOrderAddress);
        tvOrderNumber = (TextView) findViewById(R.id.tvOrderNumber);
        tvOddMoneyFrom = (TextView) findViewById(R.id.tvOddMoneyFrom);
        tvOrderApproveTime = (TextView) findViewById(R.id.tvOrderApproveTime);
        tvOrderDeliveryTime = (TextView) findViewById(R.id.tvOrderDeliveryTime);
        tvOrderCost = (TextView) findViewById(R.id.tvOrderCost);
        tvOrderInfo = (TextView) findViewById(R.id.tvOrderInfo);
        tvNotes = (TextView) findViewById(R.id.tvNotes);

        initGUIListeners();
    }

    private void initGUIListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (client != null) {
            btnCallClient.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), client.getPhone()));
        }

        btnCallOperator.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), Constants.PHONE_NUMBER_OPERATOR));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setInfoWindowAdapter(new InfoWindowAdapterImpl(getLayoutInflater()));
        map.setMyLocationEnabled(true);
        if (order == null || order.getLocation() == null || courier == null || courier.getCurrentCoordinate() == null) return;

        OrderDetailsRouteBuilder orderDetailsRouteBuilder = new OrderDetailsRouteBuilder();
        orderDetailsRouteBuilder.execute(this);
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}

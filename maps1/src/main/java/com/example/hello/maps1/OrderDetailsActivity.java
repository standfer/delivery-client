package com.example.hello.maps1;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.hello.maps1.asyncEngines.async_activities.OrderDetailsRouteBuilder;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Client;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.entities.ProductInOrder;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;
import com.example.hello.maps1.listeners.impl.BtnCallListenerImpl;
import com.example.hello.maps1.requestEngines.InfoWindowAdapterImpl;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by Ivan on 21.08.2017.
 */

public class OrderDetailsActivity extends FragmentActivity implements OnMapReadyCallback {//todo change ui and check how dynamically add tableRow and show it

    private OrderDetailsActivity orderDetailsActivity;
    private GoogleMap map;
    private Button btnBack, btnCallClient, btnCallOperator, btnOrderDetails;
    private TableLayout tableOrderDetails;
    private TableRow trOdd, trProductTitle;

    private TextView tvCourierNumber, tvClientName, tvOrderNumber, tvOddMoneyFrom, tvOrderAddressTitle, tvOrderAddress,
            tvOrderApproveTimeTitle, tvOrderApproveTime, tvOrderCost, tvOrderDeliveryTimeTitle,
            tvOrderDeliveryTime, tvNotes,
            tvOrderOddTitle, tvOrderOdd,
            tvOrderProductTitle, tvOrderProductQuantity, tvOrderProductPrice
    ;

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

        order = (Order) parameters.getSerializable(Order.class.getName());
        client = order.getClient();
        courier = (Courier) parameters.getSerializable(Courier.class.getName());

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
        btnOrderDetails = (Button) findViewById(R.id.btnOrderDetails);

        tableOrderDetails = (TableLayout) findViewById(R.id.tableOrderDetails);
        trOdd = (TableRow) findViewById(R.id.trOdd);
        trProductTitle = (TableRow) findViewById(R.id.trProductTitle);

        tvCourierNumber = (TextView) findViewById(R.id.tvCourierNumber);
        tvClientName = (TextView) findViewById(R.id.tvClientName);
        tvOrderAddress = (TextView) findViewById(R.id.tvOrderAddress);
        tvOrderNumber = (TextView) findViewById(R.id.tvOrderNumber);
        tvOddMoneyFrom = (TextView) findViewById(R.id.tvOddMoneyFrom);
        tvOrderApproveTime = (TextView) findViewById(R.id.tvOrderApproveTime);
        tvOrderDeliveryTime = (TextView) findViewById(R.id.tvOrderDeliveryTime);
        tvOrderCost = (TextView) findViewById(R.id.tvOrderCost);
        tvNotes = (TextView) findViewById(R.id.tvNotes);

        tvOrderOdd = (TextView) findViewById(R.id.tvOrderOdd);
        tvOrderProductQuantity = (TextView) findViewById(R.id.tvOrderProductQuantity);
        tvOrderProductPrice = (TextView) findViewById(R.id.tvOrderProductPrice);

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

            btnOrderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeOrderDetailsVisibility();
                    if (order == null || CollectionsHelper.isEmpty(order.getProductsInOrder())) return;

                    tvOrderOdd.setText(order.getOdd() + "");
                    for (ProductInOrder productInOrder : order.getProductsInOrder()) {
                        TextView productName = new TextView(getApplicationContext());
                        TextView productQuantity = new TextView(getApplicationContext());
                        TextView productPrice = new TextView(getApplicationContext());

                        productName.setText(productInOrder.getProduct().getName());
                        productQuantity.setText(productInOrder.getQuantity() + "");
                        productPrice.setText(productInOrder.getProduct().getPrice() + "");

                        trProductTitle.addView(productName);
                        trProductTitle.addView(productQuantity);
                        trProductTitle.addView(productPrice);
                    }
                }
            });
        }

        btnCallOperator.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), Constants.PHONE_NUMBER_OPERATOR));
    }

    private void changeOrderDetailsVisibility() {
        int visibility = View.GONE == trProductTitle.getVisibility() ? View.VISIBLE : View.GONE;
        trOdd.setVisibility(visibility);
        trProductTitle.setVisibility(visibility);

        changeMapVisibility(visibility == View.VISIBLE);//todo add hiding map
    }

    private void changeMapVisibility(boolean isVisible) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

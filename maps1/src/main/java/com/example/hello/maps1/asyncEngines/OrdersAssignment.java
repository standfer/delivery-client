package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.data_types.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;

/**
 * Created by Ivan on 10.12.2017.
 */

public class OrdersAssignment extends AsyncTask<MainMapsActivity, Void, Void> {
    protected static final String TAG = OrdersAssignment.class.getName();

    private Courier courierMain;

    @Override
    protected Void doInBackground(MainMapsActivity... mainMapsActivities) {
        try {
            MainMapsActivity mainMapsActivity = mainMapsActivities[0];
            this.courierMain = mainMapsActivity.getCourier();
            Courier courier = (Courier) courierMain.clone();//mb not need
            courier.getOrdersAvailable().clear();

            String courierInJson = JSONHelper.getJsonFromObject(courier);

            String ordersAssignRequest =
                    RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                            String.format("action=%s&courier=%s", Constants.METHOD_NAME_assignOrdersToCourier, courierInJson));

            updateCourier(courierMain);
        } catch (Throwable e) {
            Log.d(TAG, "Order assign error\n" + e.getStackTrace().toString());
        }
        return null;
    }

    protected synchronized void updateCourier(Courier srcCourier) {
        courierMain.clearAssignment(); //todo check if response successful and clear, check timeout passed for ordersAvailable
        //courierMain.setOrders(srcCourier.getOrders());
    }
}

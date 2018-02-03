package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.responses.CourierInfo;
import com.example.hello.maps1.helpers.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.StringWriter;

/**
 * Created by Ivan on 10.12.2017.
 */

public class OrdersAssignment extends AsyncTask<MainMapsActivity, Void, Void> {
    @Override
    protected Void doInBackground(MainMapsActivity... mainMapsActivities) {
        try {
            MainMapsActivity mainMapsActivity = mainMapsActivities[0];
            Courier courier = mainMapsActivity.getCourier();
            CourierInfo courierInfo = new CourierInfo(courier.getId(), courier.getOrdersToAssign());

            String courierInJson = JSONHelper.getJsonFromObject(courierInfo);

            String ordersAssignRequest =
                    RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=assignOrdersToCourier&courier=" + courierInJson);


        } catch (Throwable e) {
            Log.d("Order assign error", e.getStackTrace().toString());
        }
        return null;
    }
}

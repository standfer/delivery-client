package com.example.hello.maps1.asyncEngines.handlers;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;
import com.example.hello.maps1.helpers.data_types.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;

public class LocationHandlerThread extends HandlerThread {
    public static final String TAG = LocationHandlerThread.class.getName();

    private Handler locationRequestHandler;
    private LocationResponseHandler locationResponseHandler;
    private MainMapsActivity mainMapsActivity;

    private String responseCourierData = "", responseOrdersUnassignedData = "";

    public LocationHandlerThread(String name, MainMapsActivity mainMapsActivity) {
        super(name);
        this.mainMapsActivity = mainMapsActivity;
    }

    public void postTask() {
        locationRequestHandler.post(getRunnable());
    }

    public void prepareHandler() {
        locationRequestHandler = new Handler(getLooper());
        locationResponseHandler = new LocationResponseHandler(Looper.getMainLooper());
    }

    public Runnable getRunnable() {
        Runnable locationRunnable = new Runnable() {
            @Override
            public void run() {
                Courier courier = null;
                try {
                    courier = mainMapsActivity.getCourier();

                    courier.setOrders(null);
                    courier.clearRussianInfo();
                    courier.clearList(courier.getOrdersAvailable());
                    String jsonCourier = JSONHelper.getJsonFromObject(courier);

                    responseCourierData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                            String.format("action=%s&courier=%s", Constants.METHOD_NAME_updateCourierLocation, jsonCourier));
                    responseOrdersUnassignedData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                            String.format("action=%s&courier=%s", Constants.METHOD_NAME_getOrdersUnassigned, jsonCourier));

                    courier.updateData(responseCourierData, responseOrdersUnassignedData);

                    if (!CollectionsHelper.isEmpty(courier.getOrdersToAssign())) {
                        assignOrders(jsonCourier);
                        courier.clearList(courier.getOrdersToAssign());
                    }

                    Message msg = new Message();
                    msg.obj = mainMapsActivity;
                    locationResponseHandler.sendMessage(msg);
                } catch (Throwable e) {
                    Log.d(TAG, e.getStackTrace().toString());
                }
            }
        };
        return locationRunnable;
    }

    protected void assignOrders(String courierInJson) {
        RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                String.format("action=%s&courier=%s", Constants.METHOD_NAME_assignOrdersToCourier, courierInJson));
    }
}

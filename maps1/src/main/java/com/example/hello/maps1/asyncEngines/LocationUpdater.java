package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;
import com.example.hello.maps1.helpers.data_types.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;

@Deprecated
public class LocationUpdater extends AsyncTask<MainMapsActivity, Void, MainMapsActivity> {
    public static final String TAG = LocationUpdater.class.getName();

    private Courier courierMain;
    private MainMapsActivity mainMapsActivity;
    private String responseCourierData = "", responseOrdersUnassignedData = "";
    private String responseRouteData = "";

    @Override
    protected MainMapsActivity doInBackground(MainMapsActivity... mainMapsActivities) {
        Courier courier = null;
        try {
            this.mainMapsActivity = mainMapsActivities[0];
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
        } catch (Throwable e) {
            Log.d(TAG, e.getStackTrace().toString());
        }

        return mainMapsActivity;
    }

    @Override
    protected void onPostExecute(MainMapsActivity mainMapsActivity) {
        super.onPostExecute(mainMapsActivity);
        mainMapsActivity.updateGui(mainMapsActivity.getCourier());
    }

    protected void assignOrders(String courierInJson) {
        RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                        String.format("action=%s&courier=%s", Constants.METHOD_NAME_assignOrdersToCourier, courierInJson));
    }

    protected synchronized void updateCourier(Courier srcCourier) {
        courierMain.setOrders(srcCourier.getOrders());
        courierMain.setOrdersAvailable(srcCourier.getOrdersAvailable());
        //courierMain.setOrdersToAssign(srcCourier.getOrdersToAssign());
    }
}

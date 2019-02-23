package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;

public class LocationUpdater extends AsyncTask<Courier, Void, Courier> {
    private Courier courierMain;
    private String responseCourierData = "", responseOrdersUnassignedData = "";
    private String responseRouteData = "";

    @Override
    protected Courier doInBackground(Courier... couriers) {
        Courier courier = null;
        try {
            this.courierMain = couriers[0];
            courier = (Courier) courierMain.clone();

            courier.setOrders(null);
            String jsonCourier = JSONHelper.getJsonFromObject(courier);

            responseCourierData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS,
                    String.format("action=%s&courier=%s", Constants.METHOD_NAME_updateCourierLocation, jsonCourier));

            courier.updateData(responseCourierData, responseOrdersUnassignedData);

            /*if (courier.getCurrentCoordinate() != null) {
                responseRouteData = RequestHelper.requestRouteByCoordinates(courier.getCurrentCoordinate(), courier.getDestinationCoordinate(), courier.getOrders());
                Log.d(ToolsHelper.getLogTagByClass(LocationUpdater.class, "doInBackground"),
                        String.format("Route loaded successfully by coordinates: %s", responseCourierData));
            } else {
                Log.e("Request", String.format(Constants.ERROR_NO_DATA_FOR_COURIER, courier.getId()));
            }*/
        } catch (Throwable e) {
            Log.d("Request Error", e.getStackTrace().toString());
        }

        return null;
    }
}

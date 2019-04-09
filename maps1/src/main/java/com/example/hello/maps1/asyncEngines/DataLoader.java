package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;

import com.example.hello.maps1.SettingsActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.requestEngines.RequestHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ivan on 01.09.2017.
 */

public class DataLoader extends AsyncTask<SettingsActivity, Void, Void> { //todo change to abstract and use extending for loading any instance from server by id
    @Override
    protected Void doInBackground(SettingsActivity... fragmentActivities) {
        SettingsActivity activity = fragmentActivities[0];
        Courier courier = activity.getCourier();

        String courierRequest = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=getCourierDataById&courier=" +  RequestHelper.convertObjectToJson(activity.getCourier()));
        if (courierRequest != null) {
            try {
                JSONObject courierJson = new JSONObject(courierRequest).getJSONObject("courier");

                if (courierJson != null && courier != null) {
                    courier.setId(courierJson.getInt("id"));
                    courier.setName(courierJson.getString("name"));
                    courier.setSurname(courierJson.getString("surname"));
                    courier.setPhone(courierJson.getLong("phone"));

                    activity.initData(courier);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}

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

public class DataUpdater extends AsyncTask<SettingsActivity, Void, Void> {
    @Override
    protected Void doInBackground(SettingsActivity... fragmentActivities) {
        SettingsActivity activity = fragmentActivities[0];
        RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=updateCourierData&courier=" +  RequestHelper.convertObjectToJson(activity.getCourier()));

        return null;
    }
}

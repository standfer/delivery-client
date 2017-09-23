package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.ViewDebug;

import com.example.hello.maps1.LoginActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.requestEngines.RequestHelper;

/**
 * Created by Ivan on 01.09.2017.
 */

public class CredentialsLoader extends AsyncTask<LoginActivity, Void, Void> {
    @Override
    protected Void doInBackground(LoginActivity... loginActivities) {
        LoginActivity loginActivity = loginActivities[0];

        String courierRequest = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=getCourierIdByCredentials&courier=" +  RequestHelper.convertObjectToJson(loginActivity.getCourier()));
        if (courierRequest != null && TextUtils.isDigitsOnly(courierRequest)) {
            loginActivity.getCourier().setId(Integer.parseInt(courierRequest));
        }
        loginActivity.logOn(loginActivity.getCourier());
        return null;
    }
}

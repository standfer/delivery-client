package com.example.hello.maps1.asyncEngines;

import android.os.AsyncTask;
import android.util.Log;

import com.example.hello.maps1.LoginActivity;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.requests.Data;
import com.example.hello.maps1.entities.requests.RequestAction;
import com.example.hello.maps1.entities.responses.GetTokenResponse;
import com.example.hello.maps1.entities.responses.ResponseIlkato;
import com.example.hello.maps1.helpers.JSONHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;

/**
 * Created by Ivan on 01.09.2017.
 */

public class CredentialsLoader extends AsyncTask<LoginActivity, Void, Void> {
    @Override
    protected Void doInBackground(LoginActivity... loginActivities) {
        LoginActivity loginActivity = loginActivities[0];

        loginActivity.logOn(getSessionSSE(loginActivity.getCourier()));
        return null;
    }

    private ResponseIlkato getSessionSSE(Courier courier) {
        try {
            GetTokenResponse tokenResponse = getToken(courier);
            if (tokenResponse == null) return null;

            RequestAction requestSessionAction = new RequestAction("subscribeToUpdates", new Data("41"));
            String responseSession = RequestHelper.resultAuthPostRequest(Constants.SERVER_ADDRESS, JSONHelper.getJsonFromObject(requestSessionAction), tokenResponse.getData());

            ResponseIlkato session = (ResponseIlkato) JSONHelper.getObjectFromJson(responseSession, ResponseIlkato.class);

            Log.d(getClass().getSimpleName(), "Get session: " + (session != null ? session.getIdSession() : null));
            return session;
        } catch (Throwable ex) {
            Log.d(getClass().getSimpleName(), "Get session failed");
            return null;
        }
    }

    private GetTokenResponse getToken(Courier courier) {
        RequestAction requestAction = new RequestAction("getToken", new Data(courier));

        String responseGetToken = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, JSONHelper.getJsonFromObject(requestAction));
        GetTokenResponse tokenResponse = (GetTokenResponse) JSONHelper.getObjectFromJson(responseGetToken, GetTokenResponse.class);

        Log.d(getClass().getSimpleName(), "Get token: " + tokenResponse.getData());
        return tokenResponse;
    }
}

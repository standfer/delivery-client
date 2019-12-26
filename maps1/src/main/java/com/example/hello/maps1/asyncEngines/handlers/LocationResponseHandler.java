package com.example.hello.maps1.asyncEngines.handlers;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.hello.maps1.MainMapsActivity;

public class LocationResponseHandler extends Handler {

    public LocationResponseHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        MainMapsActivity mainMapsActivity = (MainMapsActivity) msg.obj;
        mainMapsActivity.updateGui(mainMapsActivity.getCourier());
    }
}
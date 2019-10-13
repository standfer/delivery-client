package com.example.hello.maps1.listeners.impl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.hello.maps1.helpers.LogHelper;

public class BtnSendLogsListenerImpl implements View.OnClickListener {
    protected static final String TAG = BtnSendLogsListenerImpl.class.getName();

    private Activity activity;
    private Context context;

    public BtnSendLogsListenerImpl(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        try {
            LogHelper.sendLogsEmail(context);
        }
        catch (SecurityException ex) {
            Log.d(TAG, "Unable to send logs\n" + ex.getMessage());
        }
    }
}

package com.example.hello.maps1.listeners.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.example.hello.maps1.listeners.BtnCallListener;

/**
 * Created by Ivan on 26.07.2018.
 */

public class BtnCallListenerImpl implements BtnCallListener {
    private static final String TAG = BtnCallListenerImpl.class.getName();
    private Activity activity;
    private Context context;
    private String phoneNumber;

    public BtnCallListenerImpl(Activity activity, Context context, String phoneNumber) {
        this.activity = activity;
        this.context = context;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onClick(View v) {
        //Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        //ToolsHelper.showMsgToUser(Constants.MSG_ORDER_PROBLEMS, toast);
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            activity.startActivity(callIntent);
        }
        catch (SecurityException ex) {
            Log.d(TAG, "Unable to send logs\n" + ex.getMessage());
            //ToolsHelper.showMsgToUser(Constants.MSG_CALL_FORBIDDEN, toast);
        }
    }
}

package com.example.hello.maps1.listeners.impl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.listeners.BtnCallListener;

/**
 * Created by Ivan on 26.07.2018.
 */

public class BtnCallListenerImpl implements BtnCallListener {
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
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        //ToolsHelper.showMsgToUser(Constants.MSG_ORDER_PROBLEMS, toast);
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            activity.startActivity(callIntent);
        }
        catch (SecurityException ex) {
            ToolsHelper.showMsgToUser(Constants.MSG_CALL_FORBIDDEN, toast);
        }
    }
}

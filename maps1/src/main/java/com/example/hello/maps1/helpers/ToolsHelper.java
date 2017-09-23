package com.example.hello.maps1.helpers;

import android.widget.Toast;

/**
 * Created by Ivan on 02.09.2017.
 */

public class ToolsHelper {

    public static void showMsgToUser(String msg, Toast toast) {
        toast.setText(msg);
        toast.show();
    }
}

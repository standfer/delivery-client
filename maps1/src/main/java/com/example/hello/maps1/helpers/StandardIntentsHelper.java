package com.example.hello.maps1.helpers;

import android.content.Intent;
import android.net.Uri;

import com.example.hello.maps1.MainMapsActivity;

/**
 * Created by Ivan on 19.03.2017.
 */

public class StandardIntentsHelper {

    public static void callPhoneIntent(MainMapsActivity mainMapsActivity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        mainMapsActivity.startActivity(intent);
    }
}

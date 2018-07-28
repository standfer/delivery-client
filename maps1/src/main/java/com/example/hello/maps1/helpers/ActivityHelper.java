package com.example.hello.maps1.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Ivan on 02.09.2017.
 */

public class ActivityHelper {

    public static void changeActivity(Context context, Activity oldActivity, Class newActivityClass, Object parameter) {
        Intent intent = new Intent(oldActivity, newActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_MULTIPLE_TASK*/); ////android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        try {
            if (parameter != null && TextUtils.isDigitsOnly(parameter.toString())) {
                intent.putExtra("id", (int) parameter);
            }
        }
        catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        }
        finally {
            oldActivity.finish();
            context.startActivity(intent);
        }
    }

    public static void changeActivityWithoutExit(Context context, Activity oldActivity, Class newActivityClass, Integer parameter) {
        Intent intent = new Intent(oldActivity, newActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_MULTIPLE_TASK*/); ////android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        try {
            if (parameter != null && TextUtils.isDigitsOnly(parameter.toString())) {
                intent.putExtra("id", (int) parameter);
            }
        }
        catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        }
        finally {
            //oldActivity.finish();
            context.startActivity(intent);
        }
    }

    public static void changeActivityWithoutExit(Context context, Activity oldActivity, Class newActivityClass, Serializable... parameters) {
        Intent intent = new Intent(oldActivity, newActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_MULTIPLE_TASK*/); ////android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        try {
            int i = 0;
            for (Serializable parameter : parameters) {
                if (parameter != null) {
                    if (parameter instanceof Integer) {
                        String parameterIndex = (i > 1 ? i : "").toString();
                        intent.putExtra("id" + parameterIndex, (int) parameter);
                    } else {
                        intent.putExtra(parameter.getClass().getSimpleName(), parameter);
                    }
                    i++;
                }
            }
        }
        catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        }
        finally {
            //oldActivity.finish();
            context.startActivity(intent);
        }
    }

    //added for instant run works with google maps activity, fyi: https://issuetracker.google.com/issues/66402372
    public static void addActivityToBackStack(Bundle savedInstanceState, SupportMapFragment mapFragment, FragmentManager supportFragmentManager, OnMapReadyCallback callback, @IdRes int idMap, String mapTagName) {
        //FragmentTransaction mapTransaction = supportFragmentManager.beginTransaction();
        //mapTransaction.addToBackStack(mapTagName).add(idMap, mapFragment, mapTagName).commit();

        /*if (savedInstanceState == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction mapTransaction = supportFragmentManager.beginTransaction();
            mapTransaction.addToBackStack(mapTagName).add(idMap, mapFragment, mapTagName).commit();
        }*/
        mapFragment.getMapAsync(callback); //need surely also without instant run fix

        /*//mapFragment will be destroy if not added toBackStack so, for Instant Run :
        if (savedInstanceState!=null) { mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("map"); }
        else { mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction mapTransaction = getSupportFragmentManager().beginTransaction();
            mapTransaction.addToBackStack("map").add(R.id.map,mapFragment,"map").commit(); }
        mapFragment.getMapAsync(this);*/
    }
}

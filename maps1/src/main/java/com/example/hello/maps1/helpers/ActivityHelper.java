package com.example.hello.maps1.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

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
}

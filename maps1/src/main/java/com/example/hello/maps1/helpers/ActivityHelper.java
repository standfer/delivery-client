package com.example.hello.maps1.helpers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Process;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.services.restarters.AlarmReceiver;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by Ivan on 02.09.2017.
 */

public class ActivityHelper {

    public static void changeActivity(Context context, Activity oldActivity, Class newActivityClass, Serializable parameter) {
        Intent intent = new Intent(oldActivity, newActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK /*| Intent.FLAG_ACTIVITY_MULTIPLE_TASK*/); ////android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
        try {
            if (parameter != null) {
                if (parameter instanceof Integer && TextUtils.isDigitsOnly(parameter.toString())) {
                    intent.putExtra("id", (int) parameter);
                } else {
                    intent.putExtra(parameter.getClass().getSimpleName(), parameter);
                }
            }
        } catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        } finally {
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
        } catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        } finally {
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
        } catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter from %s activity to %s activity", oldActivity.getLocalClassName(), newActivityClass.getName()));
        } finally {
            //oldActivity.finish();
            context.startActivity(intent);
        }
    }

    public static void putToIntent(Intent intent, Object... parameters) {
        try {
            int i = 0;
            for (Object parameter : parameters) {
                if (parameter != null) {
                    if (parameter instanceof Parcelable) {
                        intent.putExtra(parameter.getClass().getSimpleName(), (Parcelable) parameter);
                    } else if (parameter instanceof Serializable) {
                        intent.putExtra(parameter.getClass().getSimpleName(), (Serializable) parameter);
                    }
                }
            }
        } catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter to intent %s", intent));
        }
    }

    public static Object getFromIntent(Intent intent, Class<? extends Serializable> classObject) {
        Object result = null;

        Bundle parameters = intent.getExtras();
        if (parameters != null) {
            result = parameters.getSerializable(classObject.getSimpleName());
        }

        return result;
    }

    public static void putBytesToIntent(Intent intent, Object... parameters) {
        try {
            for (Object parameter : parameters) {
                if (parameter != null) {
                    intent.putExtra(parameter.getClass().getSimpleName(),
                            ParcelableHelper.getByteArrayFromObject(parameter));
                }
            }
        } catch (Throwable e) {
            Logger.getAnonymousLogger().warning(String.format("Can't transmit parameter to intent %s", intent));
        }
    }

    public static Object getFromIntentBytes(Intent intent, Class<? extends Serializable> classObject) {
        Object result = null;

        byte[] byteArray = intent.getByteArrayExtra(classObject.getSimpleName());

        if (byteArray != null) {
            result = ParcelableHelper.getObjectFromByteArray(byteArray);
        }

        return result;
    }

    public static Intent startService(Context context, Courier courier, Class<?> serviceClass) {
        Intent intent = new Intent(context, serviceClass);
        ActivityHelper.putToIntent(intent, (Object) courier);

        if (!isMyServiceRunning(context, serviceClass)) {
            ContextCompat.startForegroundService(context, intent);
        }

        startAlarmManager(context, courier);
        return intent;
    }

    public static void startAlarmManager(Context context, Courier courier) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        ActivityHelper.putBytesToIntent(intent, courier);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, Constants.ALARM_RECEIVER_ID, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= 23) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + Constants.ALARM_INTERVAL_FIVE_SECONDS, pendingIntent);
        } else {
            am.setExact(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + Constants.ALARM_INTERVAL_FIVE_SECONDS, pendingIntent);
        }
    }

    public static void stopService(Activity activity, Class<?> serviceClass) {
        Intent serviceIntent = new Intent(activity.getApplicationContext(), serviceClass);
        activity.stopService(serviceIntent);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(String.format("isMyService '%s' Running?", serviceClass), true + "");
                return true;
            }
        }
        Log.i(String.format("isMyService '%s' Running?", serviceClass), false + "");
        return false;
    }

    public static void stopRunningService(Activity activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                try {
                    Log.i(ActivityHelper.class.toString(),
                            String.format("My service '%s' found and trying to stop", serviceClass));
                    Process.killProcess(service.pid);
                } catch (Exception e) {
                    Log.i(ActivityHelper.class.toString(),
                            String.format("My service '%s' killed successfully", serviceClass));
                }
            }
        }
    }

    public static Intent getWhiteListIntent(Context context) {
        Intent intent = new
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                Uri.parse("package:" + context.getPackageName()));

        return intent;
    }
}

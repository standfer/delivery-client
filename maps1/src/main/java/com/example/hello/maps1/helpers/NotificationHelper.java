package com.example.hello.maps1.helpers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;

/**
 * Created by Ivan on 02.09.2017.
 */

public class NotificationHelper {
    public static void showNotification(FragmentActivity activity, String title, String messageRoute) {
        Context context = activity.getApplicationContext();

        Intent intent = new Intent(context, MainMapsActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
        Notification myNotication;

        Notification.Builder builder = new Notification.Builder(activity);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setTicker(messageRoute);
        builder.setContentTitle(title);
        builder.setContentText(messageRoute);
        builder.setSmallIcon(R.drawable.ic_cast_light);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(100);
        builder.setWhen(System.currentTimeMillis());
        //builder.getNotification();

        myNotication = builder.getNotification();
        myNotication.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);

        //timeNotification = new TimeNotification();
        //onetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(Context.ALARM_SERVICE);
        //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() /*AlarmManager.INTERVAL_DAY*/, pendingIntent);


        ////mainMapsActivity.getTimeNotification().setOnetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(ALARM_SERVICE);
    }

    public static void showNotification(Activity activity, String title, String messageRoute, Intent intent) {
        Context context = activity.getApplicationContext();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);
        Notification myNotication;

        Notification.Builder builder = new Notification.Builder(activity);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setTicker(messageRoute);
        builder.setContentTitle(title);
        builder.setContentText(messageRoute);
        builder.setSmallIcon(R.drawable.ic_cast_light);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(100);
        builder.setWhen(System.currentTimeMillis());

        myNotication = builder.getNotification();
        myNotication.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);
    }
}

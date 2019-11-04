package com.example.hello.maps1.helpers.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.NotificationCompat;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.services.TrackingService;

/**
 * Created by Ivan on 02.09.2017.
 */

public class NotificationHelper {
    public static Notification createServiceNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainMapsActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,0);
        Notification notification = null;

        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(Constants.SERVICE_NOTIFICATION_ICON)
                .setContentIntent(pendingIntent)
                .setContentTitle(Constants.SERVICE_NOTIFICATION_TITLE)
                .setContentText(Constants.SERVICE_NOTIFICATION_TEXT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, Constants.SERVICE_NOTIFICATION_CHANNEL_ID);
            builder.setChannelId(Constants.SERVICE_NOTIFICATION_CHANNEL_ID);
        }

        notification = builder.build();

        return notification;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotificationChannel(Context context, String channelId) {
        NotificationChannel channel = new NotificationChannel(
                channelId,
                TrackingService.class.getName(),
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public static Notification createServiceNotification_old(Context context) {
        Intent notificationIntent = new Intent(context, MainMapsActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(Constants.SERVICE_NOTIFICATION_ICON)
                .setContentIntent(pendingIntent)
                .setContentTitle(Constants.SERVICE_NOTIFICATION_TITLE)
                .setContentText(Constants.SERVICE_NOTIFICATION_TEXT);
        Notification notification=builder.build();
        NotificationHelper.createIfAndroidOreoNotification(notification, pendingIntent, context);

        return notification;
    }

    public static void createIfAndroidOreoNotification(Notification notification, PendingIntent pendingIntent, Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.SERVICE_NOTIFICATION_CHANNEL_ID,
                    TrackingService.class.getName(),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(TrackingService.class.getName());
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(context, Constants.SERVICE_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(Constants.SERVICE_NOTIFICATION_TITLE)
                    .setContentText(Constants.SERVICE_NOTIFICATION_TEXT)
                    .setSmallIcon(Constants.SERVICE_NOTIFICATION_ICON)
                    .setContentIntent(pendingIntent)
                    .build();
        }
    }

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
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal);
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
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(100);
        builder.setWhen(System.currentTimeMillis());

        myNotication = builder.getNotification();
        myNotication.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);
    }
}

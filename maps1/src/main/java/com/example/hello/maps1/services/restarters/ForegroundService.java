package com.example.hello.maps1.services.restarters;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;

import org.joda.time.DateTime;

public class ForegroundService extends Service {
    public static int ONGOING_NOTIFICATION_ID = 121;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void startInForeground() {
        Intent notificationIntent = new Intent(this, MainMapsActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_cast_light)
                .setContentIntent(pendingIntent)
                .setContentTitle("Service")
                .setContentText("Running...");
        Notification notification=builder.build();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(ForegroundService.class.getSimpleName(), "Foreground service onStartCommand!!");
        super.onStartCommand(intent, flags, startId);
        if (intent.getExtras() == null) return START_STICKY;

        MyThread thread = new MyThread();
        thread.start();

        startInForeground();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(1000);
                    Log.i(ForegroundService.class.getSimpleName(), "Current dateTime: " + new DateTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

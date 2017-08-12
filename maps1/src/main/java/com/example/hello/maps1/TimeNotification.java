package com.example.hello.maps1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Lyubov_Kutergina on 07.04.2016.
 */
public class TimeNotification extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    private MainMapsActivity mainMapsActivity;
    private NetworkDAO networkDAO;

    public TimeNotification(){}
    public TimeNotification(MainMapsActivity mainMapsActivity, NetworkDAO networkDAO){
        this.mainMapsActivity = mainMapsActivity;
        this.networkDAO = networkDAO;

    }


    @Override
    public void onReceive(Context context, Intent intent){
        /*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        //Toast.makeText(context,"Sheduled", Toast.LENGTH_LONG).show();*/

        //networkDAO = new NetworkDAO();
        //networkDAO.execute();

        //Release the lock
        //wl.release();

        // Запускаем свой IntentService
        Intent intentMyIntentService = new Intent(context, MyIntentService.class);
        intentMyIntentService.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //MainMapsActivity myObj = new MainMapsActivity();
        //intentMyIntentService.putExtra("MainMapsActivity", (Parcelable) mainMapsActivity);
        //MainMapsActivity mainMapsActivity = (MainMapsActivity)context;

        context.startService(intentMyIntentService.putExtra("time", 3).putExtra("task", "Погладить кота"));

    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeNotification.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, pi);

    }

    public void SetAlarm(Context context)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeNotification.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after 5 seconds

        int hour = 6, minute=50, second = 0;

        GregorianCalendar date = new GregorianCalendar();
        date.set(GregorianCalendar.HOUR_OF_DAY, hour);
        date.set(GregorianCalendar.MINUTE, minute);
        date.set(GregorianCalendar.SECOND, second);

        am.setRepeating(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        /*Calendar calendar = new Calendar() {
            @Override
            public void add(int field, int value) {

            }

            @Override
            protected void computeFields() {

            }

            @Override
            protected void computeTime() {

            }

            @Override
            public int getGreatestMinimum(int field) {
                return 0;
            }

            @Override
            public int getLeastMaximum(int field) {
                return 0;
            }

            @Override
            public int getMaximum(int field) {
                return 0;
            }

            @Override
            public int getMinimum(int field) {
                return 0;
            }

            @Override
            public void roll(int field, boolean increment) {

            }
        };
        calendar.set(2016, 5, 3,22,10);*/
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, TimeNotification.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}

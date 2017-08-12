package com.example.hello.hello2;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import com.example.hello.classes.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.widget.Toast;


import java.util.Date;


public class MainActivity extends AppCompatActivity {

    Button btnOk, btnCancel;
    EditText etText;
    private AlarmManagerBroadcastReceiver alarm;

    private static final int NOTIFY_ID=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarm = new AlarmManagerBroadcastReceiver();

//init my variables
        btnOk=(Button)findViewById(R.id.btnOk);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        etText=(EditText)findViewById(R.id.editText);
        etText.setInputType(InputType.TYPE_NULL);

        btnCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                if(alarm != null){
                    alarm.CancelAlarm(context);
                }else{
                    Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            Counter counter = new Counter();

            @Override
            public void onClick(View v) {
                counter.setCount(counter.getCount() + 1);


                //проверка - вывод уведомления
                Context context = getApplicationContext();

                //Intent notificationIntent = new Intent(context, MainActivity.class);
                //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
/*
                Notification.Builder builder = new Notification.Builder(context)
                        .setTicker("сообщение о маршруте")
                        .setWhen(System.currentTimeMillis())
                                //.setAutoCancel(true)
                        .setContentTitle("Маршрут до работы")
                        .setContentText("сообщение о маршруте");
                Notification notification = builder.getNotification();
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(101, notification);*/

                /*NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notif = new Notification(0,"Text in status bar",System.currentTimeMillis());
                notif.contentIntent = contentIntent;
                notif.tickerText = "tickerText";
                notif.flags |= Notification.FLAG_AUTO_CANCEL;
                nm.notify(1,notif);*/
//рабочая ниже
                /*NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification myNotication;


                Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");

                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification.Builder builder = new Notification.Builder(MainActivity.this);

                builder.setAutoCancel(false);
                builder.setTicker("this is ticker text");
                builder.setContentTitle("WhatsApp Notification");
                builder.setContentText("You have a new message");
                builder.setSmallIcon(R.drawable.ic_cast_light);
                builder.setContentIntent(pendingIntent);
                builder.setOngoing(true);
                //builder.setSubText("This is subtext...");   //API level 16
                builder.setNumber(100);
                //builder.getNotification();

                myNotication = builder.getNotification();
                manager.notify(11, myNotication);
                */

                /*Intent intent = new Intent(context, this.getClass());
                intent.putExtra("testName", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);*/


                if (alarm != null) {
                    alarm.setOnetimeTimer(context);
                    Toast.makeText(context, "SetAlarm called", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
                }

                etText.setText("Hello! I've done this!!!" + counter.getCount());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etText.setText("myText");
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.hello.maps1.helpers.tools;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class DeliveryTimer extends Timer { //todo refactor for using if need
    private Timer timer;
    private TimerTask timerTask;
    private int counter;

    public DeliveryTimer(int counter) {
        this.counter = counter;
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    public void stopTimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

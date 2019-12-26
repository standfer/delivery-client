package com.example.hello.maps1.helpers.tools;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.hello.maps1.R;

import java.util.concurrent.TimeUnit;

public class MyActivity extends Activity {//example, todo remove this class

    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);

        mWorkerThread = new MyWorkerThread("myWorkerThread");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyActivity.this,
                                "Background task is completed",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        };
        mWorkerThread.start();
        mWorkerThread.prepareHandler();
        mWorkerThread.postTask(task);
        mWorkerThread.postTask(task);
    }

    protected void example1() {
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        Handler requestHandler = new Handler(handlerThread.getLooper());

        final Handler responseHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //txtView.setText((String) msg.obj);
                Toast.makeText(MyActivity.this,
                        "Runnable on HandlerThread is completed and got result:"+(String)msg.obj,
                        Toast.LENGTH_LONG)
                        .show();
            }
        };

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {

                /* Add your business logic here and construct the
                   Messgae which should be handled in UI thread. For
                   example sake, just sending a simple Text here*/

                    String text = "test";
                    Message msg = new Message();

                    msg.obj = text.toString();
                    responseHandler.sendMessage(msg);
                    System.out.println(text.toString());
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        requestHandler.post(myRunnable);
    }

    @Override
    protected void onDestroy() {
        mWorkerThread.quit();
        super.onDestroy();
    }
}


class MyWorkerThread extends HandlerThread {

    private Handler mWorkerHandler;

    public MyWorkerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task){
        mWorkerHandler.post(task);
    }

    public void prepareHandler(){
        mWorkerHandler = new Handler(getLooper());
    }
}

package com.example.hello.maps1;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hello.maps1.asyncEngines.CredentialsLoader;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.services.TrackingService;
import com.rohitss.uceh.UCEHandler;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Ivan on 21.08.2017.
 */

public class LoginActivity extends FragmentActivity {

    Intent trackingIntent;
    private TrackingService trackingService;

    public int counter=0;

    private LoginActivity loginActivity;
    private Button btnLogin;
    private EditText etLogin, etPassword;
    View.OnClickListener btnLoginOnClickListener;
    private Courier courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new UCEHandler.Builder(this).build();

        setContentView(R.layout.activity_login);

        this.loginActivity = this;
        initGui();
    }

    @Override
    protected void onPause() {
        Log.i(LoginActivity.class.getSimpleName(), "LoginActivity onPause!");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(LoginActivity.class.getSimpleName(), "LoginActivity onStop!");
        super.onStop();
    }

    private void initGUIListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courier = new Courier(etLogin.getText().toString(), etPassword.getText().toString());
                try {
                    CredentialsLoader credentialsLoader = new CredentialsLoader();
                    credentialsLoader.execute(loginActivity);
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        etLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etLogin.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etPassword.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    private void initGui() {
        btnLogin = (Button) findViewById(R.id.btnLogin);

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        initGUIListeners();
    }

    public void openMainActivity(int idCourier) {
        Intent mainMapsIntent = new Intent(LoginActivity.this, MainMapsActivity.class);
        mainMapsIntent.putExtra("idCourier", idCourier);
        startActivity(mainMapsIntent);
    }

    public void logOn(Courier courier) {
        if (courier.getId() != 0) {
            ActivityHelper.changeActivity(getApplicationContext(), this, MainMapsActivity.class, courier.getId());
        } else {
            //Toast.makeText(getApplicationContext(), "Неверный логин или пароль",Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etLogin.setBackgroundColor(Color.RED);
                    etPassword.setBackgroundColor(Color.RED);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (trackingIntent != null) {
            stopService(trackingIntent);
        }
        Log.i(LoginActivity.class.getSimpleName(), "onDestroy!");
        super.onDestroy();
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in LoginActivityTimer ----  "+ (counter++));
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}

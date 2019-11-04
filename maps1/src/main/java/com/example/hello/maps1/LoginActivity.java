package com.example.hello.maps1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hello.maps1.asyncEngines.CredentialsLoader;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.LogHelper;
import com.example.hello.maps1.helpers.activities.ActivityHelper;
import com.rohitss.uceh.UCEHandler;

import androidx.fragment.app.FragmentActivity;


/**
 * Created by Ivan on 21.08.2017.
 */

public class LoginActivity extends FragmentActivity {

    private LoginActivity loginActivity;
    private Button btnLogin;
    private EditText etLogin, etPassword;
    private Courier courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new UCEHandler.Builder(this).build();
        LogHelper.createLogFile(true, getClass().getSimpleName());

        setContentView(R.layout.activity_login);

        this.loginActivity = this;
        initGui();
    }

    @Override
    protected void onPause() {
        Log.d(LoginActivity.class.getName(), "LoginActivity onPause!");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LoginActivity.class.getName(), "LoginActivity onStop!");
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

    public void logOn(Courier courier) {
        if (courier.getId() != 0) {
            ActivityHelper.changeActivity(getApplicationContext(), this, MainMapsActivity.class, courier.getId());
        } else {
            Toast.makeText(getApplicationContext(), "Неверный логин или пароль",Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etLogin.setBackgroundColor(Color.RED);
                    etPassword.setBackgroundColor(Color.RED);
                }
            });
        }
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}

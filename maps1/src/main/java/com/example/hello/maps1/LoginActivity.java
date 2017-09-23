package com.example.hello.maps1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hello.maps1.asyncEngines.CredentialsLoader;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;


/**
 * Created by Ivan on 21.08.2017.
 */

public class LoginActivity extends FragmentActivity {

    private LoginActivity loginActivity;
    private Button btnLogin;
    private EditText etLogin, etPassword;
    View.OnClickListener btnLoginOnClickListener;
    private Courier courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.loginActivity = this;
        initGui();
    }

    private void initGUIListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courier = new Courier(etLogin.getText().toString(), etPassword.getText().toString());
                try {
                    CredentialsLoader credentialsLoader = new CredentialsLoader();
                    credentialsLoader.execute(loginActivity);
                    /*if (courier.getId() != 0) {
                        openMainActivity(courier.getId());
                    } else {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль",Toast.LENGTH_SHORT).show();
                        etLogin.setBackgroundColor(Color.RED);
                        etPassword.setBackgroundColor(Color.RED);
                    }*/
                }
                catch (Throwable e) {
                    String err = e.toString();
                }

                //setContentView(R.layout.activity_main_maps);
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
        btnLogin = (Button) findViewById(R.id.btnLogin);
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

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}

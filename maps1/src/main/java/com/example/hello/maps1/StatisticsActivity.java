package com.example.hello.maps1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hello.maps1.entities.Courier;


/**
 * Created by Ivan on 21.08.2017.
 */

public class StatisticsActivity extends FragmentActivity {

    private StatisticsActivity statisticsActivity;
    private Button btnLogin;
    private EditText etLogin, etPassword;
    View.OnClickListener btnLoginOnClickListener;
    private Courier courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        this.statisticsActivity = this;
    }

}

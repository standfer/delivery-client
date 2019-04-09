package com.example.hello.maps1;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.hello.maps1.entities.Courier;


/**
 * Created by Ivan on 21.08.2017.
 */

public class RulesActivity extends FragmentActivity {

    private RulesActivity rulesActivity;
    private Button btnLogin;
    private EditText etRules, etRulesContent;
    View.OnClickListener btnLoginOnClickListener;
    private Courier courier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        this.rulesActivity = this;
    }

    private void initComponents() {
        etRules = (EditText) findViewById(R.id.etRules);
        etRulesContent = (EditText) findViewById(R.id.etRulesContent);

        etRules.setEnabled(false);
        etRulesContent.setEnabled(false);
    }

}

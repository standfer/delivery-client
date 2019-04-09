package com.example.hello.maps1;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hello.maps1.asyncEngines.DataLoader;
import com.example.hello.maps1.asyncEngines.DataUpdater;
import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.helpers.StringHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.listeners.impl.BtnCallListenerImpl;


/**
 * Created by Ivan on 21.08.2017.
 */
//todo unfocus textbox fields after editing and saving
public class SettingsActivity extends FragmentActivity {

    private SettingsActivity settingsActivity;
    private Button btnSave, btnSchedule, btnRules, btnCallOperator;
    private EditText etName, etSurname, etPhone, etInternalNumber;
    private Courier courier;
    private boolean isChanged = false;

    private Toast toast;

    @Override
    protected void onResume() {
        super.onResume();
//        loadData();
//        updateGui();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.settingsActivity = this;
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        int idCourier = getIntent ().getExtras() != null ? (int) getIntent().getExtras().get("id") : 0;
        courier = new Courier(idCourier);

        initGui();
        loadData();
    }

    private void initGui() {
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSchedule = (Button) findViewById(R.id.btnSchedule);
        btnRules = (Button) findViewById(R.id.btnRules);
        btnCallOperator = (Button) findViewById(R.id.btnCallOperator);

        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etInternalNumber = (EditText) findViewById(R.id.etInternalNumber);

        initGUIListeners();
    }

    private void updateGui() {
        if (courier != null) {
            if (!StringHelper.isEmpty(courier.getName())) etName.setText(courier.getName());
            if (!StringHelper.isEmpty(courier.getSurname())) etSurname.setText(courier.getSurname());
            if (courier.getPhone() != null) etPhone.setText(courier.getPhone().toString());
        }
    }

    private void loadData() {
        DataLoader dataLoader = new DataLoader();
        dataLoader.execute(this);
    }

    private void updateData() {
        DataUpdater dataUpdater = new DataUpdater();
        dataUpdater.execute(this);
    }

    private void initGUIListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isChanged) {
                        //RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=updateCourierData&courier=" + RequestHelper.convertObjectToJson(courier)); //todo move to DataLoader because cant request in main thread
                        updateData();
                        ToolsHelper.showMsgToUser(Constants.MSG_SAVE_SUCCESSFULL, toast);
                    }
                }
                catch (Throwable e) {
                    String err = e.toString();
                }

                //setContentView(R.layout.activity_main_maps);
            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), settingsActivity, ScheduleActivity.class, 0);
            }
        });

        btnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), settingsActivity, RulesActivity.class, 0);
            }
        });

        btnCallOperator.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), Constants.PHONE_NUMBER_OPERATOR));
    }

    private void initEditListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCourierState();
            }
        });

        etSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCourierState();
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCourierState();
            }
        });
    }

    public void initData(final Courier loadedCourier) {
        if (loadedCourier == null) return;
        this.courier.setName(loadedCourier.getName());
        this.courier.setSurname(loadedCourier.getSurname());
        this.courier.setPhone(loadedCourier.getPhone());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (etName == null) initGui();
                etName.setText(loadedCourier.getName()); //todo here cant changed from other thread
                etSurname.setText(loadedCourier.getSurname());
                etPhone.setText(loadedCourier.getPhone() != null ? loadedCourier.getPhone().toString() : "");
                etInternalNumber.setText(loadedCourier.getId() + "");

                initEditListeners();
            }
        });
    }

    private void updateCourierState() {
        isChanged = true;

        courier.setName(etName.getText().toString());
        courier.setSurname(etSurname.getText().toString());
        courier.setPhone(Long.parseLong(etPhone.getText().toString()));
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }
}

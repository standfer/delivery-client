package com.example.hello.maps1;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.helpers.activities.ActivityHelper;
import com.example.hello.maps1.helpers.activities.PermissionsHelper;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;
import com.example.hello.maps1.listeners.impl.BtnCallListenerImpl;
import com.example.hello.maps1.listeners.impl.BtnSendLogsListenerImpl;
import com.example.hello.maps1.requestEngines.InfoWindowAdapterImpl;
import com.example.hello.maps1.services.LocationUpdatesService;
import com.example.hello.maps1.services.TrackingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.nullwire.trace.ExceptionHandler;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainMapsActivity extends AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = MainMapsActivity.class.getName();

    private GoogleMap mMap;
    public static Marker mrkCurrentPos;
    private Toast toast;
    private MainMapsActivity mainMapsActivity;
    private List<Coordinate> routePoints;

    private Courier courier;
    private double coordinateCounter = 0.001;

    private boolean timerActive;
    private TimeNotification timeNotification;

    private LocationUpdatesService mService = null;
    private boolean mBound = false;
    private LocationReceiver locationReceiver; // The BroadcastReceiver used to listen from broadcasts from the service.

    //GUI
    private Button btnHelp, btnCall, btnSendLogs, btnManualMove, btnMoveToNextOrder, btnLogout;
    private Button btnOrder1, btnOrder2, btnOrder3, btnOrder4;
    private ImageButton btnFullScreen;

    private LinearLayout layoutHorizontal;
    private RelativeLayout layoutMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationReceiver = new LocationReceiver();
        Log.d(TAG, "MainMapsActivity created");

        try {
            ExceptionHandler.register(this, Constants.SERVER_LOGS_URL);
            ActivityHelper.startWhiteListAddingActivities(this);

            this.mainMapsActivity = this; //todo check if we need just send this instead of mainMapsActivity
            initGUI();

            courier = new Courier(getIntent());
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToolsHelper.initSharedPreferences(this);

        Intent locationUpdateIntent = new Intent(this, LocationUpdatesService.class);
        ActivityHelper.putToIntent(locationUpdateIntent, courier, mainMapsActivity);
        bindService(locationUpdateIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (mService != null && !ToolsHelper.isRequestingLocationUpdates(this)) {
            mService.requestLocationUpdates(this, mainMapsActivity);
        }

        //todo add update gui, think mb not need
        updateGui(courier);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));

    }

    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be used.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            setmMap(googleMap);
            LatLng initPosition = new LatLng(53.123829, 50.0947843);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 10));
            //setMrkCurrentPos(getmMap().addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Моё Текущее положение").visible(false)));
            mMap.setPadding(0, Constants.MAP_PADDING_TOP_HALF_SCREEN, 70, 0);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);

            Coordinate origin = new Coordinate(getMrkCurrentPos().getPosition().latitude, getMrkCurrentPos().getPosition().longitude);//стартовая позиция

            googleMap.setInfoWindowAdapter(new InfoWindowAdapterImpl(getLayoutInflater()));
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Location service connected");
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.requestLocationUpdates(getApplicationContext(), mainMapsActivity);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Location service disconnected");
            mService = null;
            mBound = false;
        }
    };

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receive location by broadcastReceiver");
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Log.d(TAG, "Received successfully location by broadcastReceiver");
                Toast.makeText(MainMapsActivity.this, ToolsHelper.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initGUI() {
        setContentView(R.layout.activity_main_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Fragment mapFragment = getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setHasOptionsMenu(true);
        //mapFragment.getMapAsync(this);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        btnCall = (Button) findViewById(R.id.btnCall);
        btnSendLogs = (Button) findViewById(R.id.btnSendLogs);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnManualMove = (Button) findViewById(R.id.btnManualMove);
        btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);

        btnOrder1 = (Button) findViewById(R.id.btnOrder1);
        btnOrder2 = (Button) findViewById(R.id.btnOrder2);
        btnOrder3 = (Button) findViewById(R.id.btnOrder3);
        btnOrder4 = (Button) findViewById(R.id.btnOrder4);

        layoutHorizontal = (LinearLayout) findViewById(R.id.layoutHorizontal);
        layoutHorizontal.setVisibility(View.VISIBLE);
        layoutMap = (RelativeLayout) findViewById(R.id.layoutMap);

        initGUIListeners();
        PermissionsHelper.initMainActivityPermissions(this);//todo check everywhere onRequestPermissionsResult - in case user change permissions while program works
    }

    private void initGUIListeners() {
        btnManualMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("Courier coordinates changed"));
                //workaround if current position could not be find, must be deleted
                /*if (courier != null && courier.getCurrentCoordinate() == null) {
                    courier.setCurrentCoordinate(new Coordinate(53.144657,50.0168557));
                }*/
                if (courier != null && courier.getCurrentCoordinate() != null) {
                    courier.setCurrentCoordinate(new Coordinate(courier.getCurrentCoordinate().getLat() - coordinateCounter,
                            courier.getCurrentCoordinate().getLng() - coordinateCounter));
                    coordinateCounter += 0.001;
                    courier.requestDataFromServer(mainMapsActivity);
                }
            }
        });

        btnCall.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), Constants.PHONE_NUMBER_OPERATOR));
        btnSendLogs.setOnClickListener(new BtnSendLogsListenerImpl(this, getApplicationContext()));

        /*btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToolsHelper.showMsgToUser(Constants.MSG_ORDER_PROBLEMS, toast);
                if ("Включить отслеживание".equals(btnCall.getText())) {
                    btnCall.setText("Выключить отслеживание");
                    mService.requestLocationUpdates(getApplicationContext());
                } else {
                    btnCall.setText("Включить отслеживание");
                    mService.removeLocationUpdates();
                }
            }
        });*/

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHelper.changeActivity(getApplicationContext(), mainMapsActivity, LoginActivity.class, 0);
            }
        });

        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layoutMap.getLayoutParams();
                float mapNewWeight; //= layoutParams.weight == 100 ? 1 : 100;
                int paddingTop;

                if (layoutParams.weight == 100) {
                    mapNewWeight = 1;
                    paddingTop = Constants.MAP_PADDING_TOP_FULL_SCREEN;
                } else {
                    mapNewWeight = 100;
                    paddingTop = Constants.MAP_PADDING_TOP_HALF_SCREEN;
                }

                LinearLayout.LayoutParams mapNewLayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mapNewWeight
                );

                layoutMap.setLayoutParams(mapNewLayoutParams);

                mMap.setPadding(0, paddingTop, 70, 0);
            }
        });

        layoutMap.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    mMap.setPadding(0, btnFullScreen.getTop(), 70, 0);
                } catch (Throwable ex) {
                    ToolsHelper.logException(ex);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_orders:
                ToolsHelper.showMsgToUser("Заказы выбраны", toast);
                return true;
            case R.id.action_settings:
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, SettingsActivity.class, mainMapsActivity.getCourier().getId());
                return true;
            case R.id.action_statistics:
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, StatisticsActivity.class, 0);
                return true;
            case R.id.action_rules:
                //ToolsHelper.showMsgToUser("Правила выбраны", toast);
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, RulesActivity.class, 0);
                return true;
            case R.id.action_schedule:
                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, ScheduleActivity.class, 0);
                return true;
            case R.id.action_exit:
                ActivityHelper.changeActivity(getApplicationContext(), mainMapsActivity, LoginActivity.class, 0);
                ActivityHelper.stopRunningService(this, TrackingService.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Requesting permissions result");
        if (requestCode == Constants.REQUEST_CODE_PERMISSION_ALL && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && mService != null) {
                mService.requestLocationUpdates(this, mainMapsActivity);
            } else {
                PermissionsHelper.initMainActivityPermissions(this);
            }
        }
    }

    public synchronized void updateGui(final Courier courier) {
        try {
            mainMapsActivity.setCourier(courier);
            courier.tryToAssignAvailableOrders(mainMapsActivity);

            /*btnOrder1.setVisibility(View.INVISIBLE);
            btnOrder2.setVisibility(View.INVISIBLE);
            btnOrder3.setVisibility(View.INVISIBLE);
            btnOrder4.setVisibility(View.INVISIBLE);*/

            if (!Courier.isReady(courier)) {
                Log.e(TAG, "Update GUI failed. Courier is not loaded correctly");
                return;
            }

            ToolsHelper.logOrders(MainMapsActivity.class, "updateGui", courier.getOrders(), courier.getOrdersAvailable());
            Log.d(TAG, String.format("courier.orders:%s, courier.ordersAvailable:%s",
                    !CollectionsHelper.isEmpty(courier.getOrders()) ? courier.getOrders().size() : null,
                    !CollectionsHelper.isEmpty(courier.getOrdersAvailable()) ? courier.getOrdersAvailable().size() : null));

            List<Order> orders = courier.getOrders();
            int countOrders = Math.min(4, orders.size());

            for (int i = 0; i < countOrders; i++) {
                Log.d(TAG, String.format("add orders buttons. Size:%s", !CollectionsHelper.isEmpty(orders) ? orders.size() : null));
                final Order order = orders.get(i);

                switch (i) {
                    case 0: {
                        btnOrder1.setOnClickListener(new View.OnClickListener() {//todo use the only listener
                            @Override
                            public void onClick(View v) {
                                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, OrderDetailsActivity.class, order, courier);
                            }
                        });
                        btnOrder1.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 1: {
                        btnOrder2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, OrderDetailsActivity.class, order, courier);
                            }
                        });
                        btnOrder2.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 2: {
                        btnOrder3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, OrderDetailsActivity.class, order, courier);
                            }
                        });
                        btnOrder3.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 3: {
                        btnOrder4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityHelper.changeActivityWithoutExit(getApplicationContext(), mainMapsActivity, OrderDetailsActivity.class, order, courier);
                            }
                        });
                        btnOrder4.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    public void updateFromServer() {
        if (courier.getCurrentCoordinate() != null) {
            courier.requestDataFromServer(mainMapsActivity);
            updateGui(courier);

            Log.d(TAG, String.format("Courier %s coordinates has sent (lat = %s, lng = %s)",
                            courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));
        }
    }

    @Override
    protected void onPause() throws SecurityException {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop!");
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Marker getMrkCurrentPos() {
        return mrkCurrentPos;
    }

    public void setMrkCurrentPos(Marker marker) {
        this.mrkCurrentPos = marker;
    }

    public void setMrkCurrentPos(String markerTitle) {
        this.mrkCurrentPos.setTitle(markerTitle);
    }

    public TimeNotification getTimeNotification() {
        return timeNotification;
    }

    public void setTimeNotification(TimeNotification timeNotification) {
        this.timeNotification = timeNotification;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public List<Coordinate> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<Coordinate> routePoints) {
        this.routePoints = routePoints;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public boolean isTimerActive() {
        return timerActive;
    }

    public void setTimerActive(boolean timerActive) {
        this.timerActive = timerActive;
    }
}

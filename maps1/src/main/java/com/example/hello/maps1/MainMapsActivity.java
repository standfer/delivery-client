package com.example.hello.maps1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.helpers.CollectionsHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
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
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainMapsActivity extends /*FragmentActivity*/AppCompatActivity implements OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {
    protected static final String TAG = MainMapsActivity.class.getName();
    protected Intent trackingIntent;

    private GoogleMap mMap;
    public static Marker mrkCurrentPos;
    public static String getRequest;
    private String url;
    private String urlNewBridge;
    private String urlOldBridge;
    private Toast toast;
    private MainMapsActivity mainMapsActivity;
    private boolean isRouteNeed = false;//флаг, нужно ли обращаться к гуглу за маршрутом
    private List<Coordinate> routePoints;
    double distanceChanged = 0;
    private Courier courier;
    private int idCourier = 10;
    private double coordinateCounter = 0.001;
    private int orderCounter = 0;

    private boolean timerActive;

    private LocationUpdatesService mService = null;

    //GUI
    private Button btnHelp, btnCall, btnSendLogs, btnManualMove, btnMoveToNextOrder, btnLogout;
    private Button btnOrder1, btnOrder2, btnOrder3, btnOrder4;
    private ImageButton btnFullScreen;

    private LinearLayout layoutHorizontal;
    private RelativeLayout layoutMap;

    private TimeNotification timeNotification;

    private LocationManager locationManager;
    private boolean mBound = false;
    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Location service connected");
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.requestLocationUpdates(getApplicationContext());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Location service disconnected");
            mService = null;
            mBound = false;
        }
    };

    private void initGUI() {
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
        initPermissions();//todo check everywhere onRequestPermissionsResult - in case user change permissions while program works
        //initTimer();
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

    private void initPermissions() {
        int permissionLocationStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarceLocationStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCallPhoneStatus = ContextCompat.checkSelfPermission(this, Manifest.
                permission.CALL_PHONE);
        int permissionInternetStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionWakeLockStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        int permissionForegroundStatus = ContextCompat.checkSelfPermission(this, "android.permission.FOREGROUND_SERVICE");

        int permissionReadLogsStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS);
        int permissionReadExternalStorageStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorageStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionLocationStatus != PackageManager.PERMISSION_GRANTED ||
                permissionCoarceLocationStatus != PackageManager.PERMISSION_GRANTED ||
                permissionCallPhoneStatus != PackageManager.PERMISSION_GRANTED ||
                permissionInternetStatus != PackageManager.PERMISSION_GRANTED ||
                permissionWakeLockStatus != PackageManager.PERMISSION_GRANTED ||
                permissionReadLogsStatus != PackageManager.PERMISSION_GRANTED ||
                permissionReadExternalStorageStatus != PackageManager.PERMISSION_GRANTED ||
                permissionWriteExternalStorageStatus != PackageManager.PERMISSION_GRANTED ||
                permissionForegroundStatus != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Permissions requested");
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.READ_LOGS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "android.permission.FOREGROUND_SERVICE"

                    }, Constants.REQUEST_CODE_PERMISSION_ALL);
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        Log.d(TAG, "MainMapsActivity created");

        try {
            ExceptionHandler.register(this, Constants.SERVER_LOGS_URL);
            ActivityHelper.startWhiteListAddingActivities(this);

            this.mainMapsActivity = this; //todo check if we need just send this instead of mainMapsActivity
            isRouteNeed = true;
            setContentView(R.layout.activity_main_maps);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            Fragment mapFragment = getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.setHasOptionsMenu(true);
            //mapFragment.getMapAsync(this);

            initGUI();

            idCourier = getIntent().getExtras() != null ? (int) getIntent().getExtras().get("id") : 0;
            courier = new Courier(idCourier, "Vasya", 1);

            //ActivityHelper.startTrackingService(this, courier);
            //ActivityHelper.startService(getApplicationContext(), courier, TrackingService.class);

            /*trackingIntent = new Intent(getApplicationContext(), TrackingService.class);
            ActivityHelper.putToIntent(trackingIntent, (Object) courier);

            if (!isMyServiceRunning(TrackingService.class)) {
                NotificationHelper.showNotification(this, "Доставка", "Приложение работает", trackingIntent);
                startService(trackingIntent);
            }*/

            /*Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (courier != null && courier.getCurrentCoordinate() != null) {
                            courier.requestDataFromServer(mainMapsActivity);
                            Log.d("Request", String.format("Current coordinate (%s) has sent", courier.getCurrentCoordinate()));
                        }
                    } catch(Throwable ex) {
                        Log.d("Request", ex.getStackTrace().toString());
                    }
                }
            }, 1000, 15000);*/
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        Intent locationUpdateIntent = new Intent(this, LocationUpdatesService.class);
        ActivityHelper.putToIntent(locationUpdateIntent, courier);
        bindService(locationUpdateIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        if (mService != null && !ToolsHelper.isRequestingLocationUpdates(this)) {
            mService.requestLocationUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));

        if (Constants.IS_UI_REFRESH_ENABLED && locationManager != null) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

                //checkEnabled();

            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() throws SecurityException {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        if (locationManager != null) locationManager.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Requesting permissions result");
        if (requestCode == Constants.REQUEST_CODE_PERMISSION_ALL && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && mService != null) {
                mService.requestLocationUpdates(this);
            } else {
                initPermissions();
            }
        }
    }

    private class MyReceiver extends BroadcastReceiver {
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

    public synchronized void updateGui(final Courier courier) {
        try {
            ToolsHelper.logOrders(MainMapsActivity.class, "updateGui", courier.getOrders(), courier.getOrdersAvailable());
            Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "updateGui"), String.format("courier.orders:%s, courier.ordersAvailable:%s",
                    !CollectionsHelper.isEmpty(courier.getOrders()) ? courier.getOrders().size() : null,
                    !CollectionsHelper.isEmpty(courier.getOrdersAvailable()) ? courier.getOrdersAvailable().size() : null));

            if (courier == null || CollectionsHelper.isEmpty(courier.getOrders())) return;
            List<Order> orders = courier.getOrders();

            int countOrders = Math.min(4, orders.size());

            for (int i = 0; i < countOrders; i++) {
                Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "updateGui"),
                        String.format("add orders buttons. Size:%s", !CollectionsHelper.isEmpty(orders) ? orders.size() : null));
                final Order order = orders.get(i);

                switch (i) {
                    case 0: {
                        btnOrder1.setOnClickListener(new View.OnClickListener() {
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
        //if (courier.getCurrentCoordinate() == null) courier.setCurrentCoordinate(new Coordinate(53.193688, 50.185753));

        if (courier.getCurrentCoordinate() != null) {
            courier.requestDataFromServer(mainMapsActivity);
            updateGui(courier);

            Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "updateFromServer"),
                    String.format("Courier %s coordinates has sent (lat = %s, lng = %s)",
                            courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));
        }
    }

    public void initTimer() {
        Timer timer = new Timer();
        mainMapsActivity.setTimerActive(true);

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                //updateFromServer();
                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mainMapsActivity.isTimerActive()) {
                                Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "initTimer"), String.format("Courier %s currentCoordinate set", courier));
                                updateFromServer();
                            }
                        }
                    });
                } catch (Throwable ex) {
                    ToolsHelper.logException(ex);
                }
            }
        }, 1000, 5000);
    }

    private LocationListener locationListener = null; /*new LocationListener() { //TODO NOT FORGOT ABOUT PERMISSIONS FOR MAPS1 IN PHONE/SECURITY
        @Override
        public void onLocationChanged(Location location) {
            try {
                Coordinate changedLocation = new Coordinate(location);

                courier.setCurrentCoordinate(changedLocation);
                Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "OnLocationChanged"), String.format("Courier %s currentCoordinate set", courier));

                courier.requestDataFromServer(mainMapsActivity);
                updateGui(courier);

                Log.d(ToolsHelper.getLogTagByClass(MainMapsActivity.class, "OnLocationChanged"),
                        String.format("Courier %s coordinates has sent (lat = %s, lng = %s)",
                                courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));
            } catch (Throwable ex) {
                ToolsHelper.logException(ex);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (Constants.IS_SHOW_MSG_PROVIDER_CHANGED) {
                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                    toast.setText("Using: " + String.valueOf(LocationManager.GPS_PROVIDER));
                } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    toast.setText("Using: " + String.valueOf(LocationManager.NETWORK_PROVIDER));
                }
                toast.show();
            }
        }
    };*/

    private boolean isGpsLocation(Location location) {
        return location != null ? location.getProvider().equals(LocationManager.GPS_PROVIDER) : false;
    }

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            toast.setText("GPS provider" + location.toString());
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            toast.setText("Network provider" + location.toString());
            ;
        }
        toast.show();
        getMrkCurrentPos().setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        //отправляем объект с текущими координатами на сервер
        String locationToServer = new Coordinate(location).toJson();
    }

    private void checkEnabled() {
        try {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                toast.setText("Для работы с программой необходимо включить GPS!");
                toast.show();
                //отображаем меню для включения GPS
                //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        } catch (Throwable ex) {
            ToolsHelper.logException(ex);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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

        if (locationManager != null) locationManager.removeUpdates(locationListener);//todo think mb this is the reason of not sending problems, cause while go to foreground service onDestroy may be called before
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


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlNewBridge() {
        return urlNewBridge;
    }

    public void setUrlNewBridge(String urlNewBridge) {
        this.urlNewBridge = urlNewBridge;
    }

    public String getUrlOldBridge() {
        return urlOldBridge;
    }

    public void setUrlOldBridge(String urlOldBridge) {
        this.urlOldBridge = urlOldBridge;
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

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public boolean isTimerActive() {
        return timerActive;
    }

    public void setTimerActive(boolean timerActive) {
        this.timerActive = timerActive;
    }
}

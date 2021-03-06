package com.example.hello.maps1;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.gui.dialogs.OrdersDialog;
import com.example.hello.maps1.helpers.ActivityHelper;
import com.example.hello.maps1.helpers.CollectionsHelper;
import com.example.hello.maps1.helpers.ToolsHelper;
import com.example.hello.maps1.listeners.BtnCallListener;
import com.example.hello.maps1.listeners.impl.BtnCallListenerImpl;
import com.example.hello.maps1.requestEngines.InfoWindowAdapterImpl;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainMapsActivity extends /*FragmentActivity*/ActionBarActivity implements OnMapReadyCallback {

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

    //private String serverAddress = "http://185.26.113.131/ilkato/helper.php";//"http://192.168.1.5";// "http://192.168.43.185";//
    //private String serverAddress = "http://192.168.43.185";//
    //private String serverAddress = "http://192.168.1.36/delivery/helpers/helper.php";//

    //GUI
    private Button btnHelp, btnCall, btnManualMove, btnMoveToNextOrder, btnLogout;
    private Button btnOrder1, btnOrder2, btnOrder3, btnOrder4;
    private ImageButton btnFullScreen;

    private LinearLayout layoutHorizontal;
    private RelativeLayout layoutMap;
    private AutoCompleteTextView autoComplFrom, autoComplTo;

    private TimeNotification timeNotification;

    //поля для хранения GPS
    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    private void initGUI() {
        //autoComplFrom = (AutoCompleteTextView) findViewById(R.id.autoComplFrom);
        //autoComplTo = (AutoCompleteTextView) findViewById(R.id.autoComplTo);

        btnCall = (Button) findViewById(R.id.btnCall);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnManualMove = (Button) findViewById(R.id.btnManualMove);
        btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        //btnMoveToNextOrder = (Button) findViewById(R.id.btnMoveToNextOrder);

        btnOrder1 = (Button) findViewById(R.id.btnOrder1);
        btnOrder2 = (Button) findViewById(R.id.btnOrder2);
        btnOrder3 = (Button) findViewById(R.id.btnOrder3);
        btnOrder4 = (Button) findViewById(R.id.btnOrder4);

        layoutHorizontal = (LinearLayout) findViewById(R.id.layoutHorizontal);
        layoutHorizontal.setVisibility(View.VISIBLE);

        layoutMap = (RelativeLayout) findViewById(R.id.layoutMap);

        initGUIListeners();
    }

    private void initGUIListeners() {
        btnManualMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Location", String.format("Courier coordinates changed"));
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
                //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("3301214"));
                //startActivity(intent);
                //StandardIntentsHelper.callPhoneIntent(mainMapsActivity, "3301214");
            }
        });

        /*btnMoveToNextOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Location", String.format("Courier coordinates changed to next order"));
                if (courier != null && courier.getCurrentCoordinate() != null && !CollectionsHelper.isEmpty(courier.getOrders())) {
                    List<Order> orders = courier.getOrders();
                    Order orderToMove = orders.get(0);
                    if (orders.size() > orderCounter) {
                        orderToMove = orders.get(orderCounter++);
                    } else {
                        orderCounter = 0;
                    }

                    Coordinate nextCoordinate = new Coordinate(orderToMove.getLocation().getLat(), orderToMove.getLocation().getLng());

                    courier.setCurrentCoordinate(nextCoordinate);
                    courier.requestDataFromServer(mainMapsActivity);
                }
                //Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("3301214"));
                //startActivity(intent);
                //StandardIntentsHelper.callPhoneIntent(mainMapsActivity, "3301214");
            }
        });*/

        btnCall.setOnClickListener(new BtnCallListenerImpl(this, getApplicationContext(), Constants.PHONE_NUMBER_OPERATOR));

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsHelper.showMsgToUser(Constants.MSG_ORDER_PROBLEMS, toast);
                try {
                    /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("3301214"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    //StandardIntentsHelper.callPhoneIntent(mainMapsActivity, "3301214");
                }
                catch (SecurityException ex) {
                    ToolsHelper.showMsgToUser(Constants.MSG_CALL_FORBIDDEN, toast);
                }
                /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);*/
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setContentView(R.layout.activity_login);
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
                mMap.setPadding(0, btnFullScreen.getTop(), 70, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mainMapsActivity = this;
        isRouteNeed = true;
        setContentView(R.layout.activity_main_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setHasOptionsMenu(true);
        /*if (savedInstanceState != null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        } else {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction mapTransaction = getSupportFragmentManager().beginTransaction();
            mapTransaction.addToBackStack(Constants.TAG_MAPS_NAME).add(R.id.map, mapFragment, Constants.TAG_MAPS_NAME).commit();
        }*/
        mapFragment.getMapAsync(this);




        //ActivityHelper.addActivityToBackStack(savedInstanceState, mapFragment, getSupportFragmentManager(), this, R.id.map, "map");
        //timeNotification = new TimeNotification();

        initGUI();//создание кнопок и вешание listeners

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        idCourier = getIntent ().getExtras() != null ? (int) getIntent().getExtras().get("id") : 0;
        courier = new Courier(idCourier, "Vasya", 1);

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
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            checkEnabled();

        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() throws SecurityException {
        super.onPause();
        //locationManager.removeUpdates(locationListener);
    }

    public void showOrdersDialog() {
        FragmentManager fm = getSupportFragmentManager();
        OrdersDialog dialog = new OrdersDialog();
        dialog.show(fm, "my_tag");
    }

    public void updateGui(final Courier courier) {
        if (courier == null || CollectionsHelper.isEmpty(courier.getOrders())) return;
        List<Order> orders = courier.getOrders();

        int countOrders = Math.min(4, orders.size());

        for (int i = 0; i < countOrders; i++) {
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
    }

    private LocationListener locationListener = new LocationListener() { //TODO NOT FORGOT ABOUT PERMISSIONS FOR MAPS1 IN PHONE/SECURITY
        @Override
        public void onLocationChanged(Location location) {
            //if(courier.isRebuildRouteNeeded(changedLocation) || courier.getCurrentCoordinate() == null) {
            if (/*true ||*/ courier.getCurrentCoordinate() == null) {// || isGpsLocation(location) || courier.getCurrentCoordinate() == null) {
                /*location.setLatitude(location.getLatitude() + coordinateCounter);
                location.setLongitude(location.getLongitude() + coordinateCounter);
                coordinateCounter += 0.001;*/
                //Coordinate changedLocation = new Coordinate(location.getLatitude(), location.getLatitude());

                Coordinate changedLocation = new Coordinate(location);

                courier.setCurrentCoordinate(changedLocation);
                courier.requestDataFromServer(mainMapsActivity);
                updateGui(courier);

                //toast.setText("Отправка текущих координат курьера...");
                //toast.show();
                Log.d("Request", String.format("Courier %s coordinates has sent (lat = %s, lng = %s)", courier.getId(), courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng()));
            }
            /*checkEnabled();
            showLocation(location);

            Coordinate origin = new Coordinate(getMrkCurrentPos().getPosition().latitude, getMrkCurrentPos().getPosition().longitude);//стартовая позиция
            url = "https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&destination=53.1899312,50.1720527&waypoints=53.163053, 50.195551&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA";

            setUrlNewBridge("https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&waypoints=via:53.161654,50.194811&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA");
            setUrlOldBridge(String.format("https://maps.googleapis.com/maps/api/directions/json?origin="+origin.getLat()+","+origin.getLng()+
                                          "&waypoints=via:53.166325,50.073705&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA"));
            if(routePoints!= null) {
                //проверяем расстояние между текущими координатами и 1-й координатой маршрута, чтобы решить, надо ли перестраивать маршрут
                distanceChanged = origin.distanceTo(routePoints.get(0).getLat(), routePoints.get(0).getLng());
                if(distanceChanged>50) isRouteNeed = true;
                else if(distanceChanged!=0) isRouteNeed = false;
                toast.setText(Double.toString(distanceChanged));
                toast.show();
            }*/
            //if(isRouteNeed) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) throws SecurityException {
            /*checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                toast.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                toast.setText("Status: " + String.valueOf(status));
            }
            toast.show();
        }
    };

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
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toast.setText("Для работы с программой необходимо включить GPS!");
            toast.show();
            //отображаем меню для включения GPS
            //startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
        setmMap(googleMap);
        setMrkCurrentPos(getmMap().addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Моё Текущее положение").visible(false)));
        mMap.setPadding(0, Constants.MAP_PADDING_TOP_HALF_SCREEN, 70, 0);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        Coordinate origin = new Coordinate(getMrkCurrentPos().getPosition().latitude, getMrkCurrentPos().getPosition().longitude);//стартовая позиция
        url = "https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&destination=53.1899312,50.1720527&waypoints=53.163053, 50.195551&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA";


        setUrlNewBridge("https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&waypoints=via:53.161654,50.194811&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA");
        setUrlOldBridge(String.format("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.getLat() + "," + origin.getLng() +
                "&waypoints=via:53.166325,50.073705&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA"));

        googleMap.setInfoWindowAdapter(new InfoWindowAdapterImpl(getLayoutInflater()));
        //courier.requestDataFromServer(this);


        //LatLng sydney = new LatLng(-34, 151);
        //getMarker().title = mMap.addMarker(new MarkerOptions().position(sydney).title("MarkerTitle2").draggable(true));
        //getMarker().setTitle("ChangedMarkerTitle");
        //getMarker().showInfoWindow();
        //mMap.setPadding(0,0,10,10);


        //получение маршрута
        /*Coordinate origin = new Coordinate(getMrkCurrentPos().getPosition().latitude, getMrkCurrentPos().getPosition().longitude);//стартовая позиция
        url = "https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&destination=53.1899312,50.1720527&waypoints=53.163053, 50.195551&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA";
        setUrlNewBridge("https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&waypoints=via:53.161654,50.194811&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA");
        setUrlOldBridge(String.format("https://maps.googleapis.com/maps/api/directions/json?origin={0},{1}&waypoints=via:53.166325,50.073705&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA",
                origin.getLat(),origin.getLng()));

        NetworkDAO networkDAO = new NetworkDAO();
        try {
            //timeNotification = new TimeNotification(this, null);
            //timeNotification.setOnetimeTimer(this.getApplicationContext());

            //onetimeTimer();
            networkDAO.execute(this);
            //getRequest = networkDAO.request(url);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListener);
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
}

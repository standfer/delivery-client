package com.example.hello.maps1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hello.maps1.helpers.StandardIntentsHelper;
import com.example.hello.maps1.requestEngines.InfoWindowAdapterImpl;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    private double coordinateCounter = 0.001;

    //private String serverAddress = "http://185.26.113.131/ilkato/helper.php";//"http://192.168.1.5";// "http://192.168.43.185";//
    //private String serverAddress = "http://192.168.43.185";//
    private String serverAddress = "http://192.168.1.36/delivery/helpers/helper.php";//

    //GUI
    private Button btnHelp;
    private Button btnManualMove;
    View.OnClickListener btnHelpOnClickListener;
    View.OnClickListener btnManualMoveOnClickListener;
    LinearLayout layoutHorizontal;
    private AutoCompleteTextView autoComplFrom, autoComplTo;


    private TimeNotification timeNotification;

    //поля для хранения GPS
    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainMapsActivity = this;


        isRouteNeed = true;
        setContentView(R.layout.activity_main_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //timeNotification = new TimeNotification();

        initGUI();//создание кнопок и вешание listeners

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);

        courier = new Courier(10, "Vasya", 1);

        Timer timer = new Timer();
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
        }, 1000, 15000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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

    private void initGUIListeners() {
        btnManualMoveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toast.setText("Привет! Уведомление о проблемах с заказом отправлено оператору");
                toast.setText("Смена координат");
                toast.show();
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
        };

        btnHelpOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast.setText("Внимание! Уведомление о проблемах с заказом отправлено оператору");
                toast.show();
                try {
                    /*Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("3301214"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    //StandardIntentsHelper.callPhoneIntent(mainMapsActivity, "3301214");
                }
                catch (SecurityException ex) {
                    toast.setText("Call forbidden");
                    toast.show();
                }
                /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);*/
            }
        };


    }

    private void initGUI() {
        initGUIListeners();
        autoComplFrom = (AutoCompleteTextView) findViewById(R.id.autoComplFrom);
        autoComplTo = (AutoCompleteTextView) findViewById(R.id.autoComplTo);

        btnHelp = (Button) findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(btnHelpOnClickListener);

        btnManualMove = (Button) findViewById(R.id.btnManualMove);
        btnManualMove.setOnClickListener(btnManualMoveOnClickListener);

        layoutHorizontal = (LinearLayout) findViewById(R.id.layoutHorizontal);
        layoutHorizontal.setVisibility(View.VISIBLE);

        final String[] mCats = {"Мурзик", "Рыжик", "Барсик", "Борис",
                "Мурзилка", "Мурка"};
        autoComplFrom.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mCats));
        autoComplFrom.setThreshold(1);

    }


    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //if(courier.isRebuildRouteNeeded(changedLocation) || courier.getCurrentCoordinate() == null) {
            if (true || courier.getCurrentCoordinate() == null) {// || isGpsLocation(location) || courier.getCurrentCoordinate() == null) {
                /*location.setLatitude(location.getLatitude() + coordinateCounter);
                location.setLongitude(location.getLongitude() + coordinateCounter);
                coordinateCounter += 0.001;*/
                //Coordinate changedLocation = new Coordinate(location.getLatitude(), location.getLatitude());

                Coordinate changedLocation = new Coordinate(location);

                courier.setCurrentCoordinate(changedLocation);
                courier.requestDataFromServer(mainMapsActivity);

                toast.setText("Отправка текущих координат курьера...");
                toast.show();
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

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }
}

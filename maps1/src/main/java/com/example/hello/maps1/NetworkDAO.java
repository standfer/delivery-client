package com.example.hello.maps1;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;


import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.example.hello.maps1.requestEngines.RouteHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.example.hello.maps1.TimeNotification;

/**
 * Created by Lyubov_Kutergina on 31.01.2016. // not used now, instead of - MyIntentService
 */
public class NetworkDAO extends AsyncTask<MainMapsActivity, Void, Void> {//<String, Void, String> {

    public String answerOldBridge = "", answerNewBridge = "", routeToDestination = "";
    private String answerLocalhost = "";
    private MainMapsActivity mainMapsActivity;
    private AlarmManager am;
    private TimeNotification timeNotification;
    private LocationManager locationManager;
    private Toast toast;
    private List<Coordinate> routePoints;


    private String testServerResponse = "";

    private Courier courier;

    private static final int NOTIFY_ID = 101;

    public NetworkDAO() {
        //locationManager =(LocationManager) mainMapsActivity.getSystemService(Context.LOCATION_SERVICE);
    }

    public NetworkDAO(MainMapsActivity mma) {
    }

    private int routeInSeconds(String answerRoute) {
        try {
            JSONObject jsonObject = new JSONObject(answerRoute);

            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject route = new JSONObject(routes.getString(0));
            JSONArray legs = route.getJSONArray("legs");
            JSONObject legsObject = new JSONObject(legs.getString(0));

            JSONObject durationObject = legsObject.getJSONObject("duration_in_traffic");

            return Integer.parseInt(durationObject.getString("value"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    protected Void doInBackground(MainMapsActivity... mainMapsActivities) {
        this.mainMapsActivity = mainMapsActivities[0];
        this.courier = this.mainMapsActivity.getCourier();

        //for text ilkato - short Courier
        com.example.hello.maps1.entities.Courier shortCourier = new com.example.hello.maps1.entities.Courier(4, courier.getCurrentCoordinate().getLat(), courier.getCurrentCoordinate().getLng());
        Long timeDifference = System.currentTimeMillis();
        //testServerResponse = RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "action=setCourierCoords&courier=" + RequestHelper.convertObjectToJson(shortCourier));
        //courier.clear();
        testServerResponse = RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "action=updateCourierLocation&courier=" + RequestHelper.convertObjectToJson(courier));//"courier=" + RequestHelper.convertObjectToJson(courier));
        timeDifference = System.currentTimeMillis() - timeDifference;
        Log.d("Time4Request", timeDifference.toString());
        courier.setData(testServerResponse);

        //answerOldBridge = RequestHelper.request(mainMapsActivities[0].getUrlOldBridge());
        //answerNewBridge = RequestHelper.request(mainMapsActivities[0].getUrlNewBridge());
        //String serverMethodName = "getOrderDetails";

        //courier.setData(RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "action=getOrderDetails"));
        //RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "text=hello");

        routeToDestination = RequestHelper.requestRouteByCoordinates(courier.getCurrentCoordinate(), courier.getDestinationCoordinate(), courier.getOrders());


        //answerLocalhost = request("http://192.168.1.5/?action=getOneObject");//"http://developer.alexanderklimov.ru/android/emulator/");
        //courier.setData(sendDataRequest("http://192.168.1.5/?"));//"http://192.168.1.43/ilkato/helperC.php?action=setCouriers"));
        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //routePoints = MyIntentService.pointsCoordinates(answerOldBridge);

        /*//преобразуем результат во время
        int msecondsOldBridge = routeInSeconds(answerOldBridge) * 1000;
        int msecondsNewBridge = routeInSeconds(answerNewBridge) * 1000;

        String messageRoute = "";
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        if (Math.max(msecondsOldBridge, msecondsNewBridge) == msecondsNewBridge) {
            messageRoute = "old: " + sdf.format(new Date(msecondsOldBridge)) + "<" + sdf.format(new Date(msecondsNewBridge));
        } else
            messageRoute = "new: " + sdf.format(new Date(msecondsNewBridge)) + "<" + sdf.format(new Date(msecondsOldBridge));*/

        toast = Toast.makeText(mainMapsActivity.getApplicationContext(), "", Toast.LENGTH_LONG);
        changeMapsGuiData(this.mainMapsActivity, MyIntentService.pointsCoordinates(routeToDestination));

        //showNotification(messageRoute);
    }

    private void changeMapsGuiData(MainMapsActivity mainMapsActivity, List<Coordinate> routePoints) {
        mainMapsActivity.getmMap().clear();
        //mainMapsActivity.setMrkCurrentPos(messageRoute);
        mainMapsActivity.getMrkCurrentPos().showInfoWindow();

        mainMapsActivity.getmMap().addPolyline(RouteHelper.getRoutePolyline(routePoints));
        mainMapsActivity.setRoutePoints(routePoints);

        LatLng initPosition = new LatLng(routePoints.get(0).getLat(), routePoints.get(0).getLng());
        //LatLng destPosition = new LatLng(routePoints.get(routePoints.size() - 1).getLat(), routePoints.get(routePoints.size() - 1).getLng());
        mainMapsActivity.getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(initPosition, 10));

        mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(initPosition).title("Вы здесь").draggable(false));
        addWaypointMarkers(courier.getOrders(), mainMapsActivity.getmMap());

        try {
            toast.setText(courier.getDistanceToChangedLocation(courier.getDestinationCoordinate()));
            toast.show();
            //проверяем расстояние между текущими координатами местом назначения, чтобы снять заказ
            if (!courier.getOrder().isDelivered() && courier.isDestinationReached() /* || mainMapsActivity.getLocationManager().addProximityAlert();*/) {
                courier.getOrder().setDelivered(true);
                showNotification("Заказ успешно доставлен");
            }
        } catch(Throwable ex) {

        }
    }

    public void showNotification(String messageRoute) {
        Context context = mainMapsActivity.getApplicationContext();

        Intent intent = new Intent(context, MainMapsActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager) mainMapsActivity.getSystemService(mainMapsActivity.NOTIFICATION_SERVICE);
        Notification myNotication;

        Notification.Builder builder = new Notification.Builder(mainMapsActivity);
        builder.setAutoCancel(true);
        builder.setTicker(messageRoute);
        builder.setContentTitle("Test intent");
        builder.setContentText(messageRoute);
        builder.setSmallIcon(R.drawable.ic_cast_light);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(100);
        builder.setWhen(System.currentTimeMillis());
        //builder.getNotification();

        myNotication = builder.getNotification();
        myNotication.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);

        //timeNotification = new TimeNotification();
        //onetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(Context.ALARM_SERVICE);
        //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() /*AlarmManager.INTERVAL_DAY*/, pendingIntent);


        ////mainMapsActivity.getTimeNotification().setOnetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(ALARM_SERVICE);
    }

    private void addWaypointMarkers(List<Order> orders, GoogleMap map) {
        int i = 0;
        for(Order order : orders) {
            LatLng workPlacePosition = new LatLng(order.getWorkPlace().getLocation().getLat(), order.getWorkPlace().getLocation().getLng());
            LatLng orderPosition = new LatLng(order.getAddressCoordinate().getLat(), order.getAddressCoordinate().getLng());
            String workPlaceSnippet = String.format("Адрес: %s", order.getWorkPlace().getAddress());
            String orderSnippet = String.format(
                    "Адрес: %s\n" +
                    "Телефон: %s\n" +
                    "Стоимость: %s\n", order.getAddress(), order.getPhoneNumber(), order.getCost()); //testServerResponse);
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(workPlacePosition).title("Забрать из:" + i).snippet(workPlaceSnippet).draggable(false));
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(orderPosition).title("Доставить по адресу:" + i).snippet(orderSnippet).draggable(false));
            i++;
        }
    }

    public void onetimeTimer(Context context) {
        if (timeNotification != null) {
            timeNotification.setOnetimeTimer(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void restartNotify() {
        am = (AlarmManager) mainMapsActivity.getSystemService(Context.ALARM_SERVICE);

    }

}


//    private String responseGet(String urlRequest){
//        /* hc = new DefaultHttpClient();
//        ResponseHandler response = new BasicResponseHandler();
//        HttpGet http = new HttpGet("http://site.ru/api.php?login=user1&psw=1234");
////получаем ответ от сервера
//        String response = (String) hc.execute(http, response);*/
//        return "response";
//    }

    /*private String sendDataRequest(String urlRequest) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlRequest);


            connection.setRequestMethod("POST");
            //connection.setDoInput(true);
            connection.setDoOutput(true);

            connection = (HttpURLConnection) url.openConnection();
//            ObjectOutputStream ous = new ObjectOutputStream(connection.getOutputStream());
//            ous.writeObject(new Coordinate(53.5, 50.6));
//            ous.close();

            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(new Coordinate(53.5, 50.6).toJson().getBytes());
            // Read response
            StringBuilder responseSB = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            //String response = request("http://192.168.1.43/ilkato/helperC.php?action=getCouriers");
            return "readStream(in)";
            //else returnString = "failed";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null) connection.disconnect();
        }
        return "";
    }*/


//Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");
//PendingIntent pendingIntent = PendingIntent.getActivity(mainMapsActivity, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

/*Notification.Builder builder = new Notification.Builder(context)
                .setTicker(messageRoute)
                .setWhen(System.currentTimeMillis())
                //.setAutoCancel(true)
                .setContentTitle("Маршрут до работы")
                .setContentText(messageRoute);
        Notification notification = builder.getNotification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);*/
        /*Toast toast = Toast.makeText(mainMapsActivity.getApplicationContext(),
                "Пора покормить кота!",
                Toast.LENGTH_SHORT);
        toast.show();*/

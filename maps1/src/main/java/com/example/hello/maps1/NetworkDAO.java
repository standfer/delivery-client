package com.example.hello.maps1;

import android.app.AlarmManager;
import android.location.LocationManager;
import android.os.AsyncTask;


import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.NotificationHelper;
import com.example.hello.maps1.requestEngines.RequestHelper;
import com.example.hello.maps1.requestEngines.RouteHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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


    private String responseCourierData = "";

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
        try {
            this.mainMapsActivity = mainMapsActivities[0];
            this.courier = this.mainMapsActivity.getCourier();

            Long timeDifference = System.currentTimeMillis();
            //responseCourierData = RequestHelper.resultPostRequest(mainMapsActivity.getServerAddress(), "action=setCourierCoords&courier=" + RequestHelper.convertObjectToJson(shortCourier));
            responseCourierData = RequestHelper.resultPostRequest(Constants.SERVER_ADDRESS, "action=updateCourierLocation&courier=" + RequestHelper.convertObjectToJson(courier));//"courier=" + RequestHelper.convertObjectToJson(courier));
            timeDifference = System.currentTimeMillis() - timeDifference;
            Log.d("Time4Request", timeDifference.toString());
            courier.setData(responseCourierData); // add here check if a driver wants to apply new order

            if (courier.getCurrentCoordinate() != null) {
                routeToDestination = RequestHelper.requestRouteByCoordinates(courier.getCurrentCoordinate(), courier.getDestinationCoordinate(), courier.getOrders());
            } else {
                Log.d("Request", String.format(Constants.ERROR_NO_DATA_FOR_COURIER, courier.getId()));
            }
        } catch (Throwable e) {
            Log.d("Request Error", e.getStackTrace().toString());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        //routePoints = MyIntentService.pointsCoordinates(answerOldBridge);

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
                NotificationHelper.showNotification(mainMapsActivity, Constants.MSG_ORDER_TITLE, Constants.MSG_ORDER_IS_DELIVERED);
            }
        } catch(Throwable ex) {

        }
    }

    private void addWaypointMarkers(List<Order> orders, GoogleMap map) {
        int i = 0;
        for(Order order : orders) {
            LatLng orderPosition = new LatLng(order.getAddressCoordinate().getLat(), order.getAddressCoordinate().getLng());
            String orderSnippet = String.format(
                    "Адрес: %s\n" +
                            "Телефон: %s\n" +
                            "Стоимость: %s\n", order.getAddress(), order.getPhoneNumber(), order.getCost()); //responseCourierData);
            mainMapsActivity.getmMap().addMarker(new MarkerOptions().position(orderPosition).title("Доставить по адресу:" + i).snippet(orderSnippet).draggable(false));
            i++;
        }
    }

    private void addWaypointMarkersWithWorkplaces(List<Order> orders, GoogleMap map) {
        int i = 0;
        for(Order order : orders) {
            LatLng workPlacePosition = new LatLng(order.getWorkPlace().getLocation().getLat(), order.getWorkPlace().getLocation().getLng());
            LatLng orderPosition = new LatLng(order.getAddressCoordinate().getLat(), order.getAddressCoordinate().getLng());
            String workPlaceSnippet = String.format("Адрес: %s", order.getWorkPlace().getAddress());
            String orderSnippet = String.format(
                    "Адрес: %s\n" +
                    "Телефон: %s\n" +
                    "Стоимость: %s\n", order.getAddress(), order.getPhoneNumber(), order.getCost()); //responseCourierData);
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

package com.example.hello.maps1;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.hello.maps1.entities.Coordinate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lyubov_Kutergina on 03.05.2016.
 */
public class MyIntentService extends IntentService {

    public String answerOldBridge="", answerNewBridge="";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService(){
        super("ServiceMap");
    }
    public MyIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//intent.getClass().
        //this.
        answerOldBridge = request("https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&waypoints=via:53.166325,50.073705&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA");
        answerNewBridge = request("https://maps.googleapis.com/maps/api/directions/json?origin=53.1238733,50.092532&waypoints=via:53.161654,50.194811&destination=53.194435,50.112780&departure_time=now&traffic_model=best_guess&key=AIzaSyDL3x6fuef-LHFGqipd_itXaO4xwQevoYA");
        //преобразуем результат во время
        int msecondsOldBridge = routeInSeconds(answerOldBridge)*1000;
        int msecondsNewBridge = routeInSeconds(answerNewBridge)*1000;

        //double firstCoord = pointsCoordinates(answerOldBridge).keys().nextElement();

        String messageRoute = "";
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        if (Math.max(msecondsOldBridge, msecondsNewBridge)==msecondsNewBridge){
            messageRoute = sdf.format(new Date(msecondsOldBridge)) + "<" + sdf.format(new Date(msecondsNewBridge)) + " Старый мост";
        }
        else messageRoute = sdf.format(new Date(msecondsNewBridge)) + "<" + sdf.format(new Date(msecondsOldBridge)) + " Аврора";

        //mainMapsActivity.setMarker(messageRoute);
        //mainMapsActivity.getMarker().showInfoWindow();
        //MainMapsActivity.marker.setTitle(messageRoute);
        showNotification(messageRoute, intent);
    }

    private String request(String urlRequest) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlRequest);
            connection = (HttpURLConnection) url.openConnection();
            //if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            InputStream in = new BufferedInputStream(connection.getInputStream());//);url.openStream()
            return readStream(in);
            //else returnString = "failed";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //if(connection != null) connection.disconnect();
        }
        return "";
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }
    private int routeInSeconds(String answerRoute){
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
//TODO сравнить метод decodePoly с методом PolyUtil из com.google.maps.android:android-maps-utils:0.3+
    public static List<Coordinate> decodePoly(String encoded) {//декодирование массива координат - маршрута, выданного гуглом
        List<Coordinate> poly = new ArrayList<Coordinate>();
        try {
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                 result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                Coordinate p = new Coordinate((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return poly;
    }

    public static List<Coordinate> pointsCoordinates(String answerRoute){
        try {
            JSONObject jsonObject = new JSONObject(answerRoute);

            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject route = new JSONObject(routes.getString(0));
            JSONArray legs = route.getJSONArray("legs");
            JSONObject legsObject = new JSONObject(legs.getString(0));
            JSONArray steps = legsObject.getJSONArray("steps");

            JSONObject polylines = (JSONObject) route.get("overview_polyline");
            String encodedPoints = polylines.getString("points");

            List<Coordinate> routePoints = new ArrayList<>();
            routePoints = decodePoly(encodedPoints);
            /*for(int k=0;k<steps.length();k++) {
                JSONObject stepObject = new JSONObject(steps.getString(k));
                JSONObject start_location = stepObject.getJSONObject("start_location");
                JSONObject end_location = stepObject.getJSONObject("end_location");
                //routePoints.add(new Coordinate(Double.parseDouble(end_location.getString("lat")),
                //                               Double.parseDouble(end_location.getString("lng"))));
                routePoints.add(new Coordinate(Double.parseDouble(start_location.getString("lat")),
                        Double.parseDouble(start_location.getString("lng"))));
            }*/

            return routePoints;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private void showNotification(String messageRoute, Intent intent){
        Context context = this.getApplicationContext();


        //Intent intent = new Intent(context,MainMapsActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0 ,intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager manager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        Notification myNotication;


        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setTicker(messageRoute);
        builder.setContentTitle("Маршрут до работы");
        builder.setContentText(messageRoute);
        builder.setSmallIcon(R.drawable.ic_cast_light);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(100);
        builder.setWhen(System.currentTimeMillis());
        //builder.getNotification();

        myNotication = builder.getNotification();
        myNotication.defaults = Notification.DEFAULT_SOUND;// | Notification.DEFAULT_VIBRATE;

        manager.notify(11, myNotication);

        //timeNotification = new TimeNotification();
        //onetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(Context.ALARM_SERVICE);
        //am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() /*AlarmManager.INTERVAL_DAY*/, pendingIntent);


        ////mainMapsActivity.getTimeNotification().setOnetimeTimer(context);

        //AlarmManager am = (AlarmManager) mainMapsActivity.getSystemService(ALARM_SERVICE);
    }
}

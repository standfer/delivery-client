package com.example.hello.maps1.requestEngines;

import com.example.hello.maps1.constants.Constants;
import com.example.hello.maps1.entities.Coordinate;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by Ivan on 12.03.2017.
 */

public class RequestHelper {

    public static String resultPostRequest(String urlPath, String urlParameters) {
        try {
            URL url = new URL(urlPath);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(urlParameters);
            dStream.flush();
            dStream.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseOutput.append(line);
            }
            br.close();

            return responseOutput != null ? responseOutput.toString() : "";
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String request(String urlRequest) {
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
        } finally {
            if (connection != null) connection.disconnect();
        }
        return "";
    }

    public static String requestRouteByCoordinates(Coordinate origin, Coordinate destination) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format(
                    "https://maps.googleapis.com/maps/api/directions/json?origin=%s,%s" +
                    "&destination=%s,%s" +
                    "&departure_time=now&traffic_model=best_guess&key=%s",
                    origin.getLat(),
                    origin.getLng(),
                    destination.getLat(),
                    destination.getLng(),
                    Constants.google_api_key
                    ));
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
        } finally {
            if (connection != null) connection.disconnect();
        }
        return "";
    }

    public static String requestRouteByCoordinates(Coordinate origin, Coordinate destination, List<Order> orders) {
        HttpURLConnection connection = null;
        try {
            String urlBuilder = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.getLat() + "," + origin.getLng();
            for (Order order : orders) {
                urlBuilder += urlBuilder.indexOf("waypoints") == -1 ? "&waypoints=via:" : "|";

                if (orders.indexOf(order) < orders.size() - 1) {
                    //add waypoint for address order except destination coordinate
                    urlBuilder += String.format("%s,%s", order.getLocation().getLat(), order.getLocation().getLng());
                }
            }
            if (destination == null || destination.getLat() == 0 || destination.getLng() == 0) {
                destination = new Coordinate(origin.getLat(), origin.getLng());
            }
            urlBuilder += String.format("&destination=%s,%s" +
                    "&departure_time=now&traffic_model=best_guess&key=%s",
                    destination.getLat(),
                    destination.getLng(),
                    Constants.google_api_key
            );

            URL url = new URL(urlBuilder);
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
        } finally {
            if (connection != null) connection.disconnect();
        }
        return "";
    }

    public static String requestRouteByCoordinates(Coordinate origin, Coordinate destination, Coordinate... waypoints) {
        HttpURLConnection connection = null;
        try {
            String urlBuilder = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.getLat() + "," + origin.getLng();
            for (Coordinate waypoint : waypoints) {
                urlBuilder += urlBuilder.indexOf("waypoints") == -1 ? "&waypoints=via:" : "|";
                urlBuilder += String.format("%s,%s", waypoint.getLng(), waypoint.getLat());
            }
            urlBuilder += String.format("&destination=%s,%s" +
                            "&departure_time=now&traffic_model=best_guess&key=%s",
                    destination.getLat(),
                    destination.getLng(),
                    Constants.google_api_key
            );
            URL url = new URL(urlBuilder);
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
        } finally {
            if (connection != null) connection.disconnect();
        }
        return "";
    }

    public static String requestRouteByCoordinatesWithWorkplaces(Coordinate origin, Coordinate destination, List<Order> orders) {
        HttpURLConnection connection = null;
        try {
            String urlBuilder = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.getLat() + "," + origin.getLng();
            for (Order order : orders) {
                //add waypoint for workPlace
                urlBuilder += urlBuilder.indexOf("waypoints") == -1 ? "&waypoints=via:" : "|";
                urlBuilder += String.format("%s,%s|", order.getWorkPlace().getLocation().getLat(), order.getWorkPlace().getLocation().getLng());

                if (orders.indexOf(order) < orders.size()) {
                    //add waypoint for address order except destination coordinate
                    urlBuilder += String.format("%s,%s", order.getLocation().getLat(), order.getLocation().getLng());
                }
            }
            if (destination == null || destination.getLat() == 0 || destination.getLng() == 0) {
                destination = new Coordinate(origin.getLat(), origin.getLng());
            }
            urlBuilder += String.format("&destination=%s,%s" +
                            "&departure_time=now&traffic_model=best_guess&key=%s",
                    destination.getLat(),
                    destination.getLng(),
                    Constants.google_api_key
            );
            URL url = new URL(urlBuilder);
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
        } finally {
            if (connection != null) connection.disconnect();
        }
        return "";
    }

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public static String convertObjectToJson(Object object) {//сериализация объекта в json
        try {
            if (object instanceof Courier) {
                ((Courier)object).clear();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(object);
        } catch (Exception ex) {
            return ex.toString();
        }
    }

}

package com.example.hello.maps1.requestEngines;

import android.graphics.Color;
import android.util.Pair;

import com.example.hello.maps1.MyIntentService;
import com.example.hello.maps1.entities.Coordinate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by Ivan on 12.03.2017.
 */

public class RouteHelper {

    public static PolylineOptions getRoutePolyline(List<Coordinate> routePoints) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.zIndex(500);
        polylineOptions.geodesic(true);
        polylineOptions.color(Color.argb(95, 110, 210, 230));//полупрозрачность цвета
        
        Integer startColorNumber = Color.YELLOW,
            colorRed = 110, colorGreen = 210, colorBlue = 230;

        for (Coordinate coordinate : routePoints) {
            incColorNumbers(colorRed, colorGreen, colorBlue);
            polylineOptions.color(Color.argb(95, colorRed, colorGreen, colorBlue));
            polylineOptions.add(new LatLng(coordinate.getLat(), coordinate.getLng()));
        }
        return  polylineOptions;
    }

    public static PolylineOptions getRoutePolyline(Coordinate origin, Coordinate destination) {
        PolylineOptions routeLine = null;
        if (origin != null) {
            String routeToDestination = RequestHelper.requestRouteByCoordinates(origin, destination);
            List<Coordinate> routePoints = MyIntentService.pointsCoordinates(routeToDestination);

            routeLine = RouteHelper.getRoutePolyline(routePoints);
        }

        return  routeLine;
    }

    public static void addMarkersByCoordinates(GoogleMap map, List<Pair<Coordinate, String>> coordinateDatas) {
        for (Pair<Coordinate, String> coordinateData : coordinateDatas) {
            Coordinate coordinate = coordinateData.first;
            String snippet = coordinateData.second;

            LatLng position = new LatLng(coordinate.getLat(), coordinate.getLng());
            String orderSnippet = snippet;

            map.addMarker(new MarkerOptions()
                    .position(position)
                    .title(String.format("Заказ"))
                    .snippet(orderSnippet)
                    .draggable(false));
        }
    }
    
    private static void incColorNumbers(Integer colorRed, Integer colorGreen, Integer colorBlue) {
        int counter = 50;
        colorRed = colorRed + counter < 255 ? colorRed + counter : 10;
        colorGreen = colorGreen + counter < 255 ? colorGreen + counter : 110;
        colorBlue = colorBlue + counter < 255 ? colorBlue + counter : 130;
    }
}

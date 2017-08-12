package com.example.hello.maps1.requestEngines;

import android.graphics.Color;

import com.example.hello.maps1.Coordinate;
import com.google.android.gms.maps.model.LatLng;
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
    
    private static void incColorNumbers(Integer colorRed, Integer colorGreen, Integer colorBlue) {
        int counter = 50;
        colorRed = colorRed + counter < 255 ? colorRed + counter : 10;
        colorGreen = colorGreen + counter < 255 ? colorGreen + counter : 110;
        colorBlue = colorBlue + counter < 255 ? colorBlue + counter : 130;
    }
}

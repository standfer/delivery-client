package com.example.hello.maps1.entities;

/**
 * Created by Ivan on 11.03.2017.
 */

public class Route {
    private Coordinate origin;
    private Coordinate destination;
    private Coordinate waypoint;
    private String apiKey;

    private String templateRequest = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&waypoints=via:%s&destination=%s&departure_time=now" +
            "&traffic_model=best_guess&key=%s";

    public Route() {
    }

    public Route(Coordinate origin, Coordinate destination, Coordinate wayPoint, String apiKey) {
        this.origin = origin;
        this.destination = destination;
        this.apiKey = apiKey;
        this.waypoint = wayPoint != null ? wayPoint : new Coordinate(53.161654, 50.194811);
    }

    public String getGoogleRequest() {
        return String.format(this.templateRequest, this.origin, this.waypoint, this.destination, this.apiKey);
    }

    public Coordinate getOrigin() {
        return origin;
    }

    public void setOrigin(Coordinate origin) {
        this.origin = origin;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Coordinate getWaypoint() {
        return waypoint;
    }

    public void setWaypoint(Coordinate waypoint) {
        this.waypoint = waypoint;
    }
}

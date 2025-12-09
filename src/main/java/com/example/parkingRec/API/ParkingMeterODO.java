package com.example.parkingRec.API;


public class ParkingMeterODO {
    private double lat;
    private double lng;
    private String name;
    private int spaces;
    private double distanceMeters; // distance from search center

    public ParkingMeterODO(double lat, double lng, String name, int spaces) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.spaces = spaces;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getName() {
        return name;
    }

    public int getSpaces() {
        return spaces;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
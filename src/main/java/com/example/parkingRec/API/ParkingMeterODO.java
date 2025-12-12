package com.example.parkingRec.API;

public class ParkingMeterODO {

    private double lat;
    private double lng;
    private String name;
    private int spaces;
    private double distanceMeters;
    private int timeLimitMinutes;
    private double ratePerHour;

    private ParkingMeterODO() {
        // use builder
    }

    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getName() { return name; }
    public int getSpaces() { return spaces; }
    public double getDistanceMeters() { return distanceMeters; }
    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public double getRatePerHour() { return ratePerHour; }

    // Builder
    public static class Builder {
        private final ParkingMeterODO dto = new ParkingMeterODO();

        public Builder lat(double lat) {
            dto.lat = lat;
            return this;
        }

        public Builder lng(double lng) {
            dto.lng = lng;
            return this;
        }

        public Builder name(String name) {
            dto.name = name;
            return this;
        }

        public Builder spaces(int spaces) {
            dto.spaces = spaces;
            return this;
        }

        public Builder distanceMeters(double distanceMeters) {
            dto.distanceMeters = distanceMeters;
            return this;
        }

        public Builder timeLimitMinutes(int timeLimitMinutes) {
            dto.timeLimitMinutes = timeLimitMinutes;
            return this;
        }

        public Builder ratePerHour(double ratePerHour) {
            dto.ratePerHour = ratePerHour;
            return this;
        }

        public ParkingMeterODO build() {
            return dto;
        }
    }
}

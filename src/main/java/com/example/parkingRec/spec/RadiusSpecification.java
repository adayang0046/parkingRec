package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;
import com.example.parkingRec.util.DistanceUtils;

public class RadiusSpecification implements MeterSpecification {

    private final double radiusMeters;

    public RadiusSpecification(double radiusMeters) {
        this.radiusMeters = radiusMeters;
    }

    @Override
    public boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng) {
        double distance = DistanceUtils.distanceInMeters(
                userLat, userLng, meter.getLat(), meter.getLng());
        return distance <= radiusMeters;
    }
}

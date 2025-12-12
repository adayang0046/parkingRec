package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;

public class MaxRateSpecification implements MeterSpecification {

    private final double maxRatePerHour;

    public MaxRateSpecification(double maxRatePerHour) {
        this.maxRatePerHour = maxRatePerHour;
    }

    @Override
    public boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng) {
        if (maxRatePerHour <= 0) return true;
        double r = meter.getRatePerHour();
        return r == 0.0 || r <= maxRatePerHour;
    }
}

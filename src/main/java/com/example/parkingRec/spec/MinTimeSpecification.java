package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;

public class MinTimeSpecification implements MeterSpecification {

    private final int minMinutes;

    public MinTimeSpecification(int minMinutes) {
        this.minMinutes = minMinutes;
    }

    @Override
    public boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng) {
        if (minMinutes <= 0) return true;
        int t = meter.getTimeLimitMinutes();
        return t == 0 || t >= minMinutes;
    }
}

package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;

public class MeterTypeSpecification implements MeterSpecification {

    private final String typeFilter;

    public MeterTypeSpecification(String typeFilter) {
        this.typeFilter = typeFilter == null ? "" : typeFilter.trim().toLowerCase();
    }

    @Override
    public boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng) {
        if (typeFilter.isEmpty()) return true;
        String mt = meter.getMeterType();
        if (mt == null) return false;
        return mt.toLowerCase().contains(typeFilter);
    }
}

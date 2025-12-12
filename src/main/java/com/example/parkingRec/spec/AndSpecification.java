package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;

public class AndSpecification implements MeterSpecification {

    private final MeterSpecification left;
    private final MeterSpecification right;

    public AndSpecification(MeterSpecification left, MeterSpecification right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng) {
        return left.isSatisfiedBy(meter, userLat, userLng)
                && right.isSatisfiedBy(meter, userLat, userLng);
    }
}

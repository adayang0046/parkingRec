package com.example.parkingRec.spec;

import com.example.parkingRec.ladot.ParkingMeter;

public interface MeterSpecification {
    boolean isSatisfiedBy(ParkingMeter meter, double userLat, double userLng);
}

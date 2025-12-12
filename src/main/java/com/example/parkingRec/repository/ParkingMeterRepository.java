package com.example.parkingRec.repository;

import com.example.parkingRec.ladot.ParkingMeter;

import java.util.List;

public interface ParkingMeterRepository {

    List<ParkingMeter> findAll();

    // Later you could add:
    // List<ParkingMeter> findWithinRadius(double lat, double lng, double radiusMeters);
}

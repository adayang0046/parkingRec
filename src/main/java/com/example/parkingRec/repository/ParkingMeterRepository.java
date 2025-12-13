package com.example.parkingRec.repository;

import com.example.parkingRec.ladot.ParkingMeter;

import java.util.List;

public interface ParkingMeterRepository {

    List<ParkingMeter> findAll();

    // List<ParkingMeter> findWithinRadius(double lat, double lng, double radiusMeters);
}

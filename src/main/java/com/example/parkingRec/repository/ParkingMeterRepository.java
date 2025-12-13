package com.example.parkingRec.repository;

import com.example.parkingRec.ladot.ParkingMeter;

import java.util.List;

public interface ParkingMeterRepository {

    List<ParkingMeter> findAll();

}

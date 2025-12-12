package com.example.parkingRec.sort;

import java.util.List;

import com.example.parkingRec.API.ParkingMeterODO;

public interface ParkingSortStrategy {
    void sort(List<ParkingMeterODO> results);
}
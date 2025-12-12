package com.example.parkingRec.sort;

import java.util.Comparator;
import java.util.List;

import com.example.parkingRec.API.ParkingMeterODO;

public class DistanceSortStrategy implements ParkingSortStrategy {

    @Override
    public void sort(List<ParkingMeterODO> results) {
        results.sort(Comparator.comparingDouble(ParkingMeterODO::getDistanceMeters));
    }
}
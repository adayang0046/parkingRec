package com.example.parkingRec.sort;

import com.example.parkingRec.API.ParkingMeterODO;

import java.util.Comparator;
import java.util.List;

public class TimeSortStrategy implements ParkingSortStrategy {

    @Override
    public void sort(List<ParkingMeterODO> results) {
        results.sort(
                Comparator.comparingInt(ParkingMeterODO::getTimeLimitMinutes).reversed()
                          .thenComparingDouble(ParkingMeterODO::getDistanceMeters)
        );
    }
}
package com.example.parkingRec.sort;

import com.example.parkingRec.API.ParkingMeterODO;

import java.util.Comparator;
import java.util.List;

public class PriceSortStrategy implements ParkingSortStrategy {

    @Override
    public void sort(List<ParkingMeterODO> results) {
        results.sort(
                Comparator.comparingDouble((ParkingMeterODO d) -> {
                    double r = d.getRatePerHour();
                    return r > 0.0 ? r : Double.MAX_VALUE;
                }).thenComparingDouble(ParkingMeterODO::getDistanceMeters)
        );
    }
}
package com.example.parkingRec.sort;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ParkingSortStrategyFactory {

    private final Map<String, ParkingSortStrategy> strategies = new HashMap<>();

    public ParkingSortStrategyFactory() {
        strategies.put("distance", new DistanceSortStrategy());
        strategies.put("time", new TimeSortStrategy());
        strategies.put("price", new PriceSortStrategy());
    }

    public ParkingSortStrategy get(String sortBy) {
        if (sortBy == null) return strategies.get("distance");
        return strategies.getOrDefault(sortBy.toLowerCase(), strategies.get("distance"));
    }
}
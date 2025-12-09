package com.example.parkingRec.API;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ParkingAPIController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Parking Recommender!";
    }

    @GetMapping("/search")
    public List<ParkingMeterODO> search(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int radiusMeters
    ) {
        List<ParkingMeterODO> results = new ArrayList<>();

        // Dummy meters near the requested location
        results.add(new ParkingMeterODO(lat + 0.001, lng,         "Meter A", 2));
        results.add(new ParkingMeterODO(lat,         lng + 0.001, "Meter B", 3));
        results.add(new ParkingMeterODO(lat - 0.001, lng - 0.001, "Meter C", 1));

        // Compute distance from search center and set it on each DTO
        for (ParkingMeterODO meter : results) {
            double distance = distanceInMeters(lat, lng, meter.getLat(), meter.getLng());
            meter.setDistanceMeters(distance);
        }

        // Sort by distance ascending
        results.sort(Comparator.comparingDouble(ParkingMeterODO::getDistanceMeters));

        return results;
    }

    // Simple Haversine distance in meters
    private static double distanceInMeters(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000.0; // Earth radius in meters

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}




package com.example.parkingRec.API;

import com.example.parkingRec.API.ParkingMeterODO;
import com.example.parkingRec.ladot.LadotInventoryService;
import com.example.parkingRec.ladot.ParkingMeter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ParkingAPIController {

    private final LadotInventoryService inventoryService;

    public ParkingAPIController(LadotInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

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

    for (ParkingMeter meter : inventoryService.getAllMeters()) {
        double distance = distanceInMeters(lat, lng, meter.getLat(), meter.getLng());
        if (distance <= radiusMeters) {
            ParkingMeterODO dto = new ParkingMeterODO(
                    meter.getLat(),
                    meter.getLng(),
                    "Space " + meter.getSpaceId(),
                    1
            );
            dto.setDistanceMeters(distance);
            results.add(dto);
        }
    }

    results.sort(Comparator.comparingDouble(ParkingMeterODO::getDistanceMeters));
    return results;
}


    private static double distanceInMeters(double lat1, double lng1,
                                           double lat2, double lng2) {
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

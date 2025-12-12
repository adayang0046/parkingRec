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
        @RequestParam int radiusMeters,
        @RequestParam(defaultValue = "distance") String sortBy,
        @RequestParam(defaultValue = "0") int minTimeMinutes,
        @RequestParam(defaultValue = "0") double maxRatePerHour,
        @RequestParam(defaultValue = "") String meterType
) {
List<ParkingMeterODO> results = new ArrayList<>();

String sortKey = sortBy.toLowerCase();
String meterTypeFilter = meterType.trim().toLowerCase();

for (ParkingMeter meter : inventoryService.getAllMeters()) {
    double distance = distanceInMeters(
            lat, lng, meter.getLat(), meter.getLng());

    if (distance > radiusMeters) {
        continue;
    }

    int timeLimitMinutes = meter.getTimeLimitMinutes();
    double rate = meter.getRatePerHour();

    // Filter by min time
    if (minTimeMinutes > 0 && timeLimitMinutes > 0 &&
        timeLimitMinutes < minTimeMinutes) {
        continue;
    }

    // Filter by max rate
    if (maxRatePerHour > 0 && rate > 0.0 && rate > maxRatePerHour) {
        continue;
    }

    // Filter by meter type 
    if (!meterTypeFilter.isEmpty()) {
        String mt = meter.getMeterType() == null ? "" : meter.getMeterType().toLowerCase();
        if (!mt.contains(meterTypeFilter)) {
            continue;
        }
    }

    ParkingMeterODO dto = new ParkingMeterODO(
            meter.getLat(),
            meter.getLng(),
            "Space " + meter.getSpaceId(), // label
            1,                             
            timeLimitMinutes,
            rate
    );
    dto.setDistanceMeters(distance);
    results.add(dto);
}

// Sorting
switch (sortKey) {
    case "time":
        // longest stay first, then distance
        results.sort(Comparator
                .comparingInt((ParkingMeterODO d) -> d.getTimeLimitMinutes()).reversed()
                .thenComparingDouble(ParkingMeterODO::getDistanceMeters));
        break;
    case "price":
        // cheapest first, then distance
        results.sort(Comparator
                .comparingDouble((ParkingMeterODO d) -> {
                    double r = d.getRatePerHour();
                    return r > 0.0 ? r : Double.MAX_VALUE;
                })
                .thenComparingDouble(ParkingMeterODO::getDistanceMeters));
        break;
    case "distance":
    default:
        // closest first
        results.sort(Comparator.comparingDouble(ParkingMeterODO::getDistanceMeters));
        break;
}



return results;
}

    private static double distanceInMeters(double lat1, double lng1,
                                           double lat2, double lng2) {
        double R = 6371000.0; // Earth radius in m

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}

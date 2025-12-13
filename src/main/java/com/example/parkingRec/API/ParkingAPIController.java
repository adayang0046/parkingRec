package com.example.parkingRec.API;

import com.example.parkingRec.ladot.ParkingMeter;
import com.example.parkingRec.repository.ParkingMeterRepository;
import com.example.parkingRec.sort.ParkingSortStrategy;
import com.example.parkingRec.sort.ParkingSortStrategyFactory;
import com.example.parkingRec.spec.*;
import com.example.parkingRec.util.DistanceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ParkingAPIController {

    private final ParkingMeterRepository repository;
    private final ParkingSortStrategyFactory sortStrategyFactory;

    public ParkingAPIController(ParkingMeterRepository repository,
                                ParkingSortStrategyFactory sortStrategyFactory) {
        this.repository = repository;
        this.sortStrategyFactory = sortStrategyFactory;
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
        int safeRadius = Math.max(radiusMeters, 0);
        int safeMinTime = Math.max(minTimeMinutes, 0);
        double safeMaxRate = Math.max(maxRatePerHour, 0.0);

        MeterSpecification spec = buildSpecification(safeRadius, safeMinTime, safeMaxRate, meterType);

        List<ParkingMeterODO> results = new ArrayList<>();

        for (ParkingMeter meter : repository.findAll()) {
            if (!spec.isSatisfiedBy(meter, lat, lng)) {
                continue;
            }

            double distance = DistanceUtils.distanceInMeters(
                    lat, lng, meter.getLat(), meter.getLng());

            ParkingMeterODO dto = new ParkingMeterODO.Builder()
                    .lat(meter.getLat())
                    .lng(meter.getLng())
                    .name(buildName(meter))
                    .spaces(1)  // can use real data if avaliable
                    .distanceMeters(distance)
                    .timeLimitMinutes(meter.getTimeLimitMinutes())
                    .ratePerHour(meter.getRatePerHour())
                    .build();

            results.add(dto);
        }

        ParkingSortStrategy sortStrategy = sortStrategyFactory.get(sortBy);
        sortStrategy.sort(results);

        return results;
    }

    private MeterSpecification buildSpecification(int radiusMeters,
                                                  int minTimeMinutes,
                                                  double maxRatePerHour,
                                                  String meterType) {
        MeterSpecification spec = new RadiusSpecification(radiusMeters);

        if (minTimeMinutes > 0) {
            spec = new AndSpecification(spec, new MinTimeSpecification(minTimeMinutes));
        }
        if (maxRatePerHour > 0) {
            spec = new AndSpecification(spec, new MaxRateSpecification(maxRatePerHour));
        }
        if (meterType != null && !meterType.isBlank()) {
            spec = new AndSpecification(spec, new MeterTypeSpecification(meterType));
        }

        return spec;
    }

    private String buildName(ParkingMeter meter) {
        return "Space " + meter.getSpaceId();
    }
}

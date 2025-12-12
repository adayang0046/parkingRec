package com.example.parkingRec.repository;

import com.example.parkingRec.adapter.ParkingMeterRowAdapter;
import com.example.parkingRec.adapter.LadotCsvRowAdapter;
import com.example.parkingRec.ladot.ParkingMeter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LadotCsvParkingMeterRepository implements ParkingMeterRepository {

    private final List<ParkingMeter> meters = new ArrayList<>();
    private final ParkingMeterRowAdapter rowAdapter = new LadotCsvRowAdapter();

    @PostConstruct
    public void load() {
        try {
            ClassPathResource resource = new ClassPathResource("data/ladot_meters.csv");
            if (!resource.exists()) {
                throw new IllegalStateException("ladot_meters.csv not found on classpath");
            }

            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build();

                Iterable<CSVRecord> records = csvFormat.parse(reader);

                int rawCount = 0;
                int parsedCount = 0;

                for (CSVRecord record : records) {
                    rawCount++;
                    try {
                        ParkingMeter meter = rowAdapter.toParkingMeter(record);
                        if (meter != null) {
                            meters.add(meter);
                            parsedCount++;
                        }
                    } catch (Exception e) {
                        // skip bad row, optionally log
                    }
                }

                System.out.println("Raw LADOT rows: " + rawCount + ", parsed meters: " + parsedCount);
            }

            System.out.println("Loaded LADOT meters: " + meters.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load LADOT inventory CSV", e);
        }
    }

    @Override
    public List<ParkingMeter> findAll() {
        return Collections.unmodifiableList(meters);
    }
}

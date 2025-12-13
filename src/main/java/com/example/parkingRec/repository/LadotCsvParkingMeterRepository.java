package com.example.parkingRec.repository;

import com.example.parkingRec.adapter.ParkingMeterRowAdapter;
import com.example.parkingRec.ladot.ParkingMeter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log =
            LoggerFactory.getLogger(LadotCsvParkingMeterRepository.class);

    private static final String LADOT_CSV_PATH = "data/ladot_meters.csv";

    private final List<ParkingMeter> meters = new ArrayList<>();
    private final ParkingMeterRowAdapter rowAdapter;

    public LadotCsvParkingMeterRepository(ParkingMeterRowAdapter rowAdapter) {
        this.rowAdapter = rowAdapter;
    }

    @PostConstruct
    public void load() {
        ClassPathResource resource = new ClassPathResource(LADOT_CSV_PATH);

        if (!resource.exists()) {
            throw new IllegalStateException("LADOT CSV not found on classpath at: " + LADOT_CSV_PATH);
        }

        int rawCount = 0;
        int parsedCount = 0;

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                rawCount++;
                try {
                    ParkingMeter meter = rowAdapter.toParkingMeter(record);
                    if (meter != null) {
                        meters.add(meter);
                        parsedCount++;
                    }
                } catch (Exception rowEx) {
                    // Log at debug 
                    log.debug("Skipping invalid LADOT row {}: {}", rawCount, rowEx.getMessage());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load LADOT inventory from " + LADOT_CSV_PATH, e);
        }

        log.info("Loaded LADOT meters: {} (raw rows: {})", parsedCount, rawCount);
    }

    @Override
    public List<ParkingMeter> findAll() {
        return Collections.unmodifiableList(meters);
    }
}

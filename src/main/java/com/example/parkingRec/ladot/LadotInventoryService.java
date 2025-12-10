package com.example.parkingRec.ladot;

import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LadotInventoryService {

    private final List<ParkingMeter> meters = new ArrayList<>();

    @PostConstruct
    public void loadInventory() {
        try {
            // 
            ClassPathResource resource = new ClassPathResource("data/ladot_meters.csv");

            System.out.println("Resource exists? " + resource.exists());
            System.out.println("Resource filename on classpath: " + resource.getFilename());

            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {

                CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                        .setHeader()              // first record is header
                        .setSkipHeaderRecord(true)
                        .setTrim(true)
                        .build();

                Iterable<CSVRecord> records = csvFormat.parse(reader);

                int rawCount = 0;
                int parsedCount = 0;

                for (CSVRecord record : records) {
                    rawCount++;


                    // [spaceid, blockface, metertype, ratetype, raterange, timelimit, latlng]
                    String spaceId   = record.get("spaceid");
                    String blockface = record.get("blockface");
                    String meterType = record.get("metertype");
                    String rateType  = record.get("ratetype");
                    String rateRange = record.get("raterange");
                    String timeLimit = record.get("timelimit");
                    String latLng    = record.get("latlng");

                    // Log first few rows to see what latlng looks like
                    if (rawCount <= 5) {
                        System.out.println("Row " + rawCount +
                                " spaceid=" + spaceId +
                                " latlng=" + latLng);
                    }

                    if (latLng == null || latLng.isBlank()) {
                        // Skip rows with no coordinates
                        continue;
                    }

                    
                    String cleaned = latLng.replace("(", "").replace(")", "");
                    String[] parts = cleaned.split(",");
                    if (parts.length != 2) {
                        // Skip malformed latlng values
                        continue;
                    }

                    double lat;
                    double lng;
                    try {
                        lat = Double.parseDouble(parts[0].trim());
                        lng = Double.parseDouble(parts[1].trim());
                    } catch (NumberFormatException e) {
                    // Skip rows with invalid numbers
                        continue;
                    }

                    ParkingMeter meter = new ParkingMeter(
                            spaceId,
                            blockface,
                            meterType,
                            rateType,
                            rateRange,
                            timeLimit,
                            lat,
                            lng
                    );
                    meters.add(meter);
                    parsedCount++;
                }

                System.out.println("Raw LADOT rows: " + rawCount +
                        ", parsed meters: " + parsedCount);
            }

            System.out.println("Loaded LADOT meters: " + meters.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load LADOT inventory CSV", e);
        }
    }

    public List<ParkingMeter> getAllMeters() {
        return Collections.unmodifiableList(meters);
    }
}

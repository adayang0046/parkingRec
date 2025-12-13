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

                    
                // (existing logging, cleaning latLng, parsing lat/lng ...)
                    String cleaned = latLng.replace("(", "").replace(")", "");
                    String[] parts = cleaned.split(",");
                    if (parts.length != 2) {
                        continue;
                    }
                    double lat;
                    double lng;
                    try {
                        lat = Double.parseDouble(parts[0].trim());
                        lng = Double.parseDouble(parts[1].trim());
                    } catch (NumberFormatException e) {
                        continue;
                    }

                // parse time limit & rate into numeric values
                    int timeLimitMinutes = parseTimeLimitMinutes(timeLimit);
                    double ratePerHour = parseRatePerHour(rateRange);

                    ParkingMeter meter = new ParkingMeter(
                            spaceId,
                            blockface,
                            meterType,
                            rateType,
                            rateRange,
                            timeLimit,
                            lat,
                            lng,
                            timeLimitMinutes,
                            ratePerHour
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
    private int parseTimeLimitMinutes(String timeLimit) {
    if (timeLimit == null) return 0;
    String s = timeLimit.toLowerCase();

    // extract first integer in the string
    int number = 0;
    java.util.regex.Matcher m =
            java.util.regex.Pattern.compile("(\\d+)").matcher(s);
    if (m.find()) {
        number = Integer.parseInt(m.group(1));
    }

    if (s.contains("min")) {
        return number;
    }
    if (s.contains("hr") || s.contains("hour")) {
        return number * 60;
    }

    // if no unit, assume hours if big, minutes if small
    if (number >= 5) {
        return number * 60;
    }
    return number;
}

private double parseRatePerHour(String rateRange) {
    if (rateRange == null || rateRange.isBlank()) {
        return 0.0; // unknown / any
    }
    String s = rateRange.toLowerCase();

    // find the first number with optional decimal
    java.util.regex.Matcher m =
            java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)").matcher(s);

    if (m.find()) {
        try {
            return Double.parseDouble(m.group(1));
        } catch (NumberFormatException ignored) {
        }
    }

    return 0.0; // treat as unknown
}

}

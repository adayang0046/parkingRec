package com.example.parkingRec.adapter;

import com.example.parkingRec.ladot.ParkingMeter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LadotCsvRowAdapter implements ParkingMeterRowAdapter {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("(\\d+)");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    // LADOT CSV column names as constants 
    private static final String COL_SPACE_ID = "spaceid";
    private static final String COL_BLOCKFACE = "blockface";
    private static final String COL_METER_TYPE = "metertype";
    private static final String COL_RATE_TYPE = "ratetype";
    private static final String COL_RATE_RANGE = "raterange";
    private static final String COL_TIME_LIMIT = "timelimit";
    private static final String COL_LAT_LNG = "latlng";

    @Override
    public ParkingMeter toParkingMeter(CSVRecord record) {
        String latLng = record.get(COL_LAT_LNG);
        if (latLng == null || latLng.isBlank()) {
            return null;
        }

        String cleaned = latLng.replace("(", "").replace(")", "");
        String[] parts = cleaned.split(",");
        if (parts.length != 2) {
            return null;
        }

        double lat;
        double lng;
        try {
            lat = Double.parseDouble(parts[0].trim());
            lng = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }

        String spaceId   = record.get(COL_SPACE_ID);
        String blockface = record.get(COL_BLOCKFACE);
        String meterType = record.get(COL_METER_TYPE);
        String rateType  = record.get(COL_RATE_TYPE);
        String rateRange = record.get(COL_RATE_RANGE);
        String timeLimit = record.get(COL_TIME_LIMIT);

        int timeLimitMinutes = parseTimeLimitMinutes(timeLimit);
        double ratePerHour   = parseRatePerHour(rateRange);

        return new ParkingMeter(
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
    }

    private int parseTimeLimitMinutes(String timeLimit) {
        if (timeLimit == null) {
            return 0;
        }

        String s = timeLimit.toLowerCase();
        int number = extractInteger(s);

        if (s.contains("min")) {
            return number;
        }
        if (s.contains("hr") || s.contains("hour")) {
            return number * 60;
        }

        // if number is big, assume hours
        if (number >= 5) {
            return number * 60;
        }
        return number;
    }

    private double parseRatePerHour(String rateRange) {
        if (rateRange == null || rateRange.isBlank()) {
            return 0.0;
        }
        String s = rateRange.toLowerCase();
        Double value = extractDecimal(s);
        return value != null ? value : 0.0;
    }

    private int extractInteger(String s) {
        Matcher m = INTEGER_PATTERN.matcher(s);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private Double extractDecimal(String s) {
        Matcher m = DECIMAL_PATTERN.matcher(s);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }
}

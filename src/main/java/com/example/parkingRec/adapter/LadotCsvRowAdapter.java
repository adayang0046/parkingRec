package com.example.parkingRec.adapter;

import com.example.parkingRec.ladot.ParkingMeter;
import org.apache.commons.csv.CSVRecord;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LadotCsvRowAdapter implements ParkingMeterRowAdapter {

    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");

    @Override
    public ParkingMeter toParkingMeter(CSVRecord record) {
        // Match actual column names from the CSV
        String spaceId   = record.get("spaceid");
        String blockface = record.get("blockface");
        String meterType = record.get("metertype");
        String rateType  = record.get("ratetype");
        String rateRange = record.get("raterange");
        String timeLimit = record.get("timelimit");
        String latLng    = record.get("latlng");

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
        if (timeLimit == null) return 0;
        String s = timeLimit.toLowerCase();

        int number = 0;
        Matcher m = Pattern.compile("(\\d+)").matcher(s);
        if (m.find()) {
            number = Integer.parseInt(m.group(1));
        }

        if (s.contains("min")) {
            return number;
        }
        if (s.contains("hr") || s.contains("hour")) {
            return number * 60;
        }

        // fallback: if no unit, guess hours if big
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
        Matcher m = NUMBER_PATTERN.matcher(s);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0.0;
    }
}

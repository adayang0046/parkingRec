package com.example.parkingRec.adapter;

import com.example.parkingRec.ladot.ParkingMeter;
import org.apache.commons.csv.CSVRecord;

public interface ParkingMeterRowAdapter {
    ParkingMeter toParkingMeter(CSVRecord record);
}

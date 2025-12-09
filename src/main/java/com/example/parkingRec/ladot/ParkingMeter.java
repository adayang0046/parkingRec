package com.example.parkingRec.ladot;


public class ParkingMeter {
    private String spaceId;
    private String blockface;
    private String meterType;
    private String rateType;
    private String rateRange;
    private String timeLimit;
    private double lat;
    private double lng;

    public ParkingMeter(String spaceId,
                        String blockface,
                        String meterType,
                        String rateType,
                        String rateRange,
                        String timeLimit,
                        double lat,
                        double lng) {
        this.spaceId = spaceId;
        this.blockface = blockface;
        this.meterType = meterType;
        this.rateType = rateType;
        this.rateRange = rateRange;
        this.timeLimit = timeLimit;
        this.lat = lat;
        this.lng = lng;
    }

    public String getSpaceId() { return spaceId; }
    public String getBlockface() { return blockface; }
    public String getMeterType() { return meterType; }
    public String getRateType() { return rateType; }
    public String getRateRange() { return rateRange; }
    public String getTimeLimit() { return timeLimit; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}

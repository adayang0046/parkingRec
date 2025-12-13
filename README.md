# Parking Recommender

A small Java/Spring Boot + Leaflet web app that helps you explore **LADOT parking meters** around a given location.

You choose a center point by clicking the map or entering latitude/longitude, set a search radius, and filter results by **time limit** and **approximate hourly rate**. The app shows all matching meters on an interactive map.

---

## Features

-  **Interactive Leaflet map**
  - Pan/zoom around Los Angeles
  - Click on the map to set the search center
  - Or manually enter a latitude/longitude and click **Set center**

-  **Radius search**
  - Search for meters within a given radius (in meters) around the center
  - If the radius is too small and no meters are found, the app automatically retries with 500 m

-  **Filters & Sorting**
  - **Min time**: only show meters with at least X minutes/hours time limit
  - **Max rate**: hide meters more expensive than a chosen hourly rate (approximate, parsed from LADOT rate data)
  - **Sort by**:
    - `Closest` (distance)
    - `Longest stay` (time limit)
    - `Cheapest` (hourly rate)

-  **Meter details**
  - Each marker popup shows:
    - Space ID (e.g. `Space SVV434`)
    - Distance from center
    - Time limit
    - Approx hourly rate
    - Lat/Lng

-  **Backend design patterns**
  - Repository (`ParkingMeterRepository`, `LadotCsvParkingMeterRepository`)
  - Adapter (`ParkingMeterRowAdapter`, `LadotCsvRowAdapter` for LADOT CSV rows)
  - Specification (composable filters for radius, time, rate, type)
  - Strategy (sorting by distance/time/price)
  - Builder (`ParkingMeterODO.Builder` for API responses)

---

## Tech Stack

- **Backend**: Java, Spring Boot, Spring Web
- **Frontend**: HTML, CSS, JavaScript, [Leaflet](https://leafletjs.com/) + OpenStreetMap tiles
- **Data**: LADOT parking meter inventory CSV (loaded on startup via Apache Commons CSV)

---

## High Level Project Structure

```text
src/
  main/
    java/
      com/example/parkingRec/
        ParkingRecApplication.java
        api/
          ParkingAPIController.java
          ParkingMeterODO.java
        ladot/
          ParkingMeter.java
        repository/
          ParkingMeterRepository.java
          LadotCsvParkingMeterRepository.java
        adapter/
          ParkingMeterRowAdapter.java
          LadotCsvRowAdapter.java
        spec/
          MeterSpecification.java
          RadiusSpecification.java
          MinTimeSpecification.java
          MaxRateSpecification.java
          MeterTypeSpecification.java
          AndSpecification.java
        sort/
          ParkingSortStrategy.java
          DistanceSortStrategy.java
          TimeSortStrategy.java
          PriceSortStrategy.java
          ParkingSortStrategyFactory.java
        util/
          DistanceUtils.java

    resources/
      data/
        ladot_meters.csv        # LADOT inventory (not committed if large)
      static/
        index.html              # Frontend entry
        css/
          styles.css
        js/
          app.js

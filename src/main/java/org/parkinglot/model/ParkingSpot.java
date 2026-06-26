package org.parkinglot.model;

import java.util.Objects;

public class ParkingSpot {
    private final String spotId;
    private final int floorNumber;
    private final SpotSize size;
    private boolean isAvailable;

    public ParkingSpot(String spotId, int floorNumber, SpotSize size) {
        if (spotId == null || spotId.isBlank()) {
            throw new IllegalArgumentException("Spot ID is required");
        }
        if (floorNumber < 1) {
            throw new IllegalArgumentException("Floor number must be positive");
        }
        this.spotId = spotId;
        this.floorNumber = floorNumber;
        this.size = Objects.requireNonNull(size, "Spot size is required");
        this.isAvailable = true;
    }

    public synchronized boolean isAvailable() { return isAvailable; }

    public synchronized boolean reserve() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }

    public synchronized void vacate() {
        this.isAvailable = true;
    }

    // Getters
    public String getSpotId() { return spotId; }
    public int getFloorNumber() { return floorNumber; }
    public SpotSize getSize() { return size; }
}

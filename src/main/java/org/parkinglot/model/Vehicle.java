package org.parkinglot.model;

import java.util.Objects;

public class Vehicle {
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalArgumentException("License plate is required");
        }
        this.licensePlate = licensePlate;
        this.type = Objects.requireNonNull(type, "Vehicle type is required");
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
}

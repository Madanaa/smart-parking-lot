package org.parkinglot.model;

public enum SpotSize {
    SMALL, MEDIUM, LARGE;

    public boolean canFit(VehicleType vehicleType) {
        return ordinal() >= vehicleType.getRequiredSpotSize().ordinal();
    }
}

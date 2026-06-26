package org.parkinglot.strategy;


import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.VehicleType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DefaultSpotAllocationStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<ParkingSpot> allocateSpot(List<ParkingSpot> spots, VehicleType vehicleType) {
        return spots.stream()
                .filter(spot -> spot.getSize().canFit(vehicleType))
                .sorted(Comparator
                        .comparing(ParkingSpot::getSize)
                        .thenComparingInt(ParkingSpot::getFloorNumber)
                        .thenComparing(ParkingSpot::getSpotId))
                .filter(ParkingSpot::isAvailable)
                .filter(ParkingSpot::reserve)
                .findFirst();
    }
}

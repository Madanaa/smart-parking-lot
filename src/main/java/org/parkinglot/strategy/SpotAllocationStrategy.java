package org.parkinglot.strategy;


import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.VehicleType;

import java.util.List;
import java.util.Optional;

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocateSpot(List<ParkingSpot> spots, VehicleType vehicleType);
}

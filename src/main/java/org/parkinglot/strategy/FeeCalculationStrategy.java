package org.parkinglot.strategy;

import org.parkinglot.model.VehicleType;

import java.time.LocalDateTime;

public interface FeeCalculationStrategy {
    double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType);
}

package org.parkinglot.strategy;

import org.parkinglot.model.VehicleType;

import java.time.Duration;
import java.time.LocalDateTime;

public class HourlyFeeCalculationStrategy implements FeeCalculationStrategy {

    @Override
    public double calculateFee(LocalDateTime entryTime, LocalDateTime exitTime, VehicleType vehicleType) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        if (minutes < 0) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }

        long hours = Math.max(1, (minutes + 59) / 60);

        double baseRate = switch (vehicleType) {
            case MOTORCYCLE -> 10.0;
            case CAR -> 20.0;
            case BUS -> 50.0;
        };

        return hours * baseRate;
    }
}

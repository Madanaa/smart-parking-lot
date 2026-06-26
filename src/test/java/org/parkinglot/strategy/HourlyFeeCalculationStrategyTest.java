package org.parkinglot.strategy;

import org.junit.jupiter.api.Test;
import org.parkinglot.model.VehicleType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HourlyFeeCalculationStrategyTest {

    private final HourlyFeeCalculationStrategy strategy = new HourlyFeeCalculationStrategy();

    @Test
    void chargesMinimumOneHour() {
        LocalDateTime entryTime = LocalDateTime.of(2026, 6, 26, 10, 0);
        LocalDateTime exitTime = entryTime.plusMinutes(15);

        assertEquals(20.0, strategy.calculateFee(entryTime, exitTime, VehicleType.CAR));
    }

    @Test
    void roundsPartialHoursUp() {
        LocalDateTime entryTime = LocalDateTime.of(2026, 6, 26, 10, 0);
        LocalDateTime exitTime = entryTime.plusMinutes(61);

        assertEquals(40.0, strategy.calculateFee(entryTime, exitTime, VehicleType.CAR));
    }

    @Test
    void rejectsExitBeforeEntry() {
        LocalDateTime entryTime = LocalDateTime.of(2026, 6, 26, 10, 0);
        LocalDateTime exitTime = entryTime.minusMinutes(1);

        assertThrows(IllegalArgumentException.class,
                () -> strategy.calculateFee(entryTime, exitTime, VehicleType.CAR));
    }
}

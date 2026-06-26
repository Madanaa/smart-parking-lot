package org.parkinglot.strategy;

import org.junit.jupiter.api.Test;
import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.SpotSize;
import org.parkinglot.model.VehicleType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DefaultSpotAllocationStrategyTest {

    private final DefaultSpotAllocationStrategy strategy = new DefaultSpotAllocationStrategy();

    @Test
    void assignsSmallestSuitableAvailableSpot() {
        ParkingSpot largeSpot = new ParkingSpot("C-301", 3, SpotSize.LARGE);
        ParkingSpot mediumSpot = new ParkingSpot("B-201", 2, SpotSize.MEDIUM);

        ParkingSpot allocatedSpot = strategy.allocateSpot(
                List.of(largeSpot, mediumSpot),
                VehicleType.CAR
        ).orElseThrow();

        assertEquals("B-201", allocatedSpot.getSpotId());
        assertFalse(allocatedSpot.isAvailable());
    }

    @Test
    void allowsVehicleToUseLargerSpotWhenExactSizeIsUnavailable() {
        ParkingSpot largeSpot = new ParkingSpot("C-301", 3, SpotSize.LARGE);

        ParkingSpot allocatedSpot = strategy.allocateSpot(
                List.of(largeSpot),
                VehicleType.CAR
        ).orElseThrow();

        assertEquals("C-301", allocatedSpot.getSpotId());
    }

    @Test
    void rejectsSpotThatIsTooSmall() {
        ParkingSpot smallSpot = new ParkingSpot("A-101", 1, SpotSize.SMALL);

        assertFalse(strategy.allocateSpot(List.of(smallSpot), VehicleType.BUS).isPresent());
    }
}

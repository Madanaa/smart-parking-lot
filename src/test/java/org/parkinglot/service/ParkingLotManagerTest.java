package org.parkinglot.service;

import org.junit.jupiter.api.Test;
import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.ParkingTicket;
import org.parkinglot.model.SpotSize;
import org.parkinglot.model.Vehicle;
import org.parkinglot.model.VehicleType;
import org.parkinglot.repository.ParkingRepository;
import org.parkinglot.strategy.DefaultSpotAllocationStrategy;
import org.parkinglot.strategy.HourlyFeeCalculationStrategy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotManagerTest {

    @Test
    void checkInAllocatesSpotAndUpdatesAvailability() {
        ParkingLotManager manager = newManager();
        manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM));

        Optional<ParkingTicket> ticket = manager.checkIn(new Vehicle("MH-12-AA-1001", VehicleType.CAR));

        assertTrue(ticket.isPresent());
        assertEquals("B-201", ticket.get().getParkingSpot().getSpotId());
        assertEquals(0, manager.getAvailableSpotCount());
        assertTrue(manager.getActiveTicket(ticket.get().getTicketId()).isPresent());
    }

    @Test
    void checkInRejectsDuplicateActiveVehicle() {
        ParkingLotManager manager = newManager();
        manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM));
        manager.addParkingSpot(new ParkingSpot("C-301", 3, SpotSize.LARGE));

        assertTrue(manager.checkIn(new Vehicle("MH-12-AA-1001", VehicleType.CAR)).isPresent());
        assertFalse(manager.checkIn(new Vehicle("MH-12-AA-1001", VehicleType.CAR)).isPresent());
        assertEquals(1, manager.getAvailableSpotCount());
    }

    @Test
    void checkInReturnsEmptyWhenNoSuitableSpotIsAvailable() {
        ParkingLotManager manager = newManager();
        manager.addParkingSpot(new ParkingSpot("A-101", 1, SpotSize.SMALL));

        Optional<ParkingTicket> ticket = manager.checkIn(new Vehicle("MH-12-BB-2002", VehicleType.BUS));

        assertFalse(ticket.isPresent());
        assertEquals(1, manager.getAvailableSpotCount());
    }

    @Test
    void checkOutClosesTicketCalculatesFeeAndFreesSpot() {
        ParkingLotManager manager = newManager();
        manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM));
        ParkingTicket activeTicket = manager.checkIn(new Vehicle("MH-12-AA-1001", VehicleType.CAR))
                .orElseThrow();

        ParkingTicket closedTicket = manager.checkOut(activeTicket.getTicketId()).orElseThrow();

        assertNotNull(closedTicket.getExitTime());
        assertEquals(20.0, closedTicket.getFee());
        assertEquals(1, manager.getAvailableSpotCount());
        assertFalse(manager.getActiveTicket(activeTicket.getTicketId()).isPresent());
    }

    @Test
    void duplicateSpotIdsAreRejected() {
        ParkingLotManager manager = newManager();
        manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM));

        assertThrows(IllegalArgumentException.class,
                () -> manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM)));
    }

    private ParkingLotManager newManager() {
        return new ParkingLotManager(
                new ParkingRepository(),
                new DefaultSpotAllocationStrategy(),
                new HourlyFeeCalculationStrategy()
        );
    }
}

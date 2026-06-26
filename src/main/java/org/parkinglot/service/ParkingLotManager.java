package org.parkinglot.service;

import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.ParkingTicket;
import org.parkinglot.model.Vehicle;
import org.parkinglot.repository.ParkingRepository;
import org.parkinglot.strategy.FeeCalculationStrategy;
import org.parkinglot.strategy.SpotAllocationStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParkingLotManager {
    // The single, static instance of the manager
    private static ParkingLotManager instance;

    // Non-static dependencies are perfectly safe here
    private final ParkingRepository repository;
    private final SpotAllocationStrategy allocationStrategy;
    private final FeeCalculationStrategy feeStrategy;

    // Private constructor ensures instantiation only happens here
    private ParkingLotManager(ParkingRepository repository,
                              SpotAllocationStrategy allocationStrategy,
                              FeeCalculationStrategy feeStrategy) {
        this.repository = repository;
        this.allocationStrategy = allocationStrategy;
        this.feeStrategy = feeStrategy;
    }

    // Thread-safe Singleton initialization
    public static synchronized ParkingLotManager getInstance(
            ParkingRepository repository,
            SpotAllocationStrategy allocationStrategy,
            FeeCalculationStrategy feeStrategy) {
        if (instance == null) {
            instance = new ParkingLotManager(repository, allocationStrategy, feeStrategy);
        }
        return instance;
    }

    public synchronized void addParkingSpot(ParkingSpot spot) {
        repository.saveSpot(spot);
    }

    public synchronized List<ParkingSpot> getAvailableSpots() {
        return repository.getAvailableSpots();
    }

    public synchronized int getAvailableSpotCount() {
        return repository.getAvailableSpots().size();
    }

    public synchronized Optional<ParkingTicket> getActiveTicket(String ticketId) {
        return repository.getActiveTicketById(ticketId);
    }

    public synchronized Optional<ParkingTicket> checkIn(Vehicle vehicle) {
        if (repository.hasActiveTicketForLicensePlate(vehicle.getLicensePlate())) {
            System.out.println("Check-in Failed: Vehicle already parked " + vehicle.getLicensePlate());
            return Optional.empty();
        }

        Optional<ParkingSpot> allocatedSpot = allocationStrategy.allocateSpot(
                repository.getAllSpots(), vehicle.getType()
        );

        if (allocatedSpot.isEmpty()) {
            System.out.println("Check-in Failed: No available spot for " + vehicle.getLicensePlate());
            return Optional.empty();
        }

        ParkingSpot spot = allocatedSpot.get();
        String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, spot);

        repository.saveTicket(ticket);
        System.out.println("Vehicle " + vehicle.getLicensePlate() + " parked at Spot " + spot.getSpotId());
        return Optional.of(ticket);
    }

    public synchronized Optional<ParkingTicket> checkOut(String ticketId) {
        Optional<ParkingTicket> ticketOpt = repository.removeTicket(ticketId);
        if (ticketOpt.isEmpty()) {
            System.out.println("Invalid Ticket ID: " + ticketId);
            return Optional.empty();
        }

        ParkingTicket ticket = ticketOpt.get();
        LocalDateTime exitTime = LocalDateTime.now();

        double calculatedFee = feeStrategy.calculateFee(
                ticket.getEntryTime(),
                exitTime,
                ticket.getVehicle().getType()
        );

        ticket.closeTicket(exitTime, calculatedFee);
        ticket.getParkingSpot().vacate(); // Free up the spot

        System.out.println("Vehicle " + ticket.getVehicle().getLicensePlate()
                + " checked out. Total Fee: Rs. " + calculatedFee);
        return Optional.of(ticket);
    }
}

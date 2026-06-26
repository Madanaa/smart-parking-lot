package org.parkinglot.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("Ticket ID is required");
        }
        this.ticketId = ticketId;
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle is required");
        this.spot = Objects.requireNonNull(spot, "Parking spot is required");
        this.entryTime = LocalDateTime.now();
    }

    public void closeTicket(LocalDateTime exitTime, double fee) {
        this.exitTime = Objects.requireNonNull(exitTime, "Exit time is required");
        this.fee = fee;
    }

    // Getters
    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getParkingSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getFee() { return fee; }
}

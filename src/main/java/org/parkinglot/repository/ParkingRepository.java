package org.parkinglot.repository;

import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.ParkingTicket;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParkingRepository {
    // Thread-safe collections to handle concurrent read/writes
    private final List<ParkingSpot> allSpots = new CopyOnWriteArrayList<>();
    private final Map<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();

    // --- Parking Spot Operations ---

    public void saveSpot(ParkingSpot spot) {
        boolean spotExists = allSpots.stream()
                .anyMatch(existingSpot -> existingSpot.getSpotId().equals(spot.getSpotId()));
        if (spotExists) {
            throw new IllegalArgumentException("Parking spot already exists: " + spot.getSpotId());
        }
        allSpots.add(spot);
    }

    public List<ParkingSpot> getAllSpots() {
        return List.copyOf(allSpots);
    }

    public List<ParkingSpot> getAvailableSpots() {
        return allSpots.stream()
                .filter(ParkingSpot::isAvailable)
                .toList();
    }

    // --- Ticket Operations ---

    public void saveTicket(ParkingTicket ticket) {
        activeTickets.put(ticket.getTicketId(), ticket);
    }

    public Optional<ParkingTicket> getActiveTicketById(String ticketId) {
        return Optional.ofNullable(activeTickets.get(ticketId));
    }

    public boolean hasActiveTicketForLicensePlate(String licensePlate) {
        return activeTickets.values().stream()
                .anyMatch(ticket -> ticket.getVehicle().getLicensePlate().equals(licensePlate));
    }

    public Optional<ParkingTicket> removeTicket(String ticketId) {
        return Optional.ofNullable(activeTickets.remove(ticketId));
    }
}

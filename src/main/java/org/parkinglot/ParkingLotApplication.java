package org.parkinglot;


import org.parkinglot.model.ParkingSpot;
import org.parkinglot.model.ParkingTicket;
import org.parkinglot.model.SpotSize;
import org.parkinglot.model.Vehicle;
import org.parkinglot.model.VehicleType;
import org.parkinglot.repository.ParkingRepository;
import org.parkinglot.service.ParkingLotManager;
import org.parkinglot.strategy.DefaultSpotAllocationStrategy;
import org.parkinglot.strategy.FeeCalculationStrategy;
import org.parkinglot.strategy.HourlyFeeCalculationStrategy;
import org.parkinglot.strategy.SpotAllocationStrategy;

public class ParkingLotApplication {
    public static void main(String[] args) {
        // 1. Initialize the interchangeable strategies
        SpotAllocationStrategy allocationStrategy = new DefaultSpotAllocationStrategy();
        FeeCalculationStrategy feeStrategy = new HourlyFeeCalculationStrategy();
        ParkingRepository repository = new ParkingRepository();

        // 2. Instantiate the thread-safe core engine via dependency injection
        ParkingLotManager manager = ParkingLotManager.getInstance(repository, allocationStrategy, feeStrategy);

        // 3. Populate initial system inventory data
        manager.addParkingSpot(new ParkingSpot("A-101", 1, SpotSize.SMALL));
        manager.addParkingSpot(new ParkingSpot("B-201", 2, SpotSize.MEDIUM));
        manager.addParkingSpot(new ParkingSpot("C-301", 3, SpotSize.LARGE));
        manager.addParkingSpot(new ParkingSpot("C-302", 3, SpotSize.LARGE));

        System.out.println("--- Smart Parking Lot Engine Initialized Successfully ---");
        printAvailability(manager);

        ParkingTicket motorcycleTicket = manager.checkIn(new Vehicle("MH-12-AA-1001", VehicleType.MOTORCYCLE))
                .orElseThrow();
        ParkingTicket carTicket = manager.checkIn(new Vehicle("MH-12-BB-2002", VehicleType.CAR))
                .orElseThrow();
        ParkingTicket busTicket = manager.checkIn(new Vehicle("MH-12-CC-3003", VehicleType.BUS))
                .orElseThrow();
        ParkingTicket overflowCarTicket = manager.checkIn(new Vehicle("MH-12-DD-4004", VehicleType.CAR))
                .orElseThrow();

        printAvailability(manager);

        manager.checkIn(new Vehicle("MH-12-EE-5005", VehicleType.CAR));
        manager.getActiveTicket(carTicket.getTicketId())
                .ifPresent(ticket -> System.out.println("Active ticket found: " + ticket.getTicketId()));

        manager.checkOut(motorcycleTicket.getTicketId()).ifPresent(ParkingLotApplication::printReceipt);
        manager.checkOut(carTicket.getTicketId()).ifPresent(ParkingLotApplication::printReceipt);
        manager.checkOut(busTicket.getTicketId()).ifPresent(ParkingLotApplication::printReceipt);
        manager.checkOut(overflowCarTicket.getTicketId()).ifPresent(ParkingLotApplication::printReceipt);

        printAvailability(manager);
    }

    private static void printAvailability(ParkingLotManager manager) {
        System.out.println("Available spots: " + manager.getAvailableSpotCount());
        manager.getAvailableSpots().forEach(spot ->
                System.out.println(" - " + spot.getSpotId() + " | Floor " + spot.getFloorNumber()
                        + " | " + spot.getSize()));
    }

    private static void printReceipt(ParkingTicket ticket) {
        System.out.println("Receipt: " + ticket.getTicketId()
                + " | Vehicle " + ticket.getVehicle().getLicensePlate()
                + " | Spot " + ticket.getParkingSpot().getSpotId()
                + " | Exit " + ticket.getExitTime()
                + " | Fee Rs. " + ticket.getFee());
    }
}

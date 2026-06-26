# Smart Parking Lot LLD

Backend low-level design for a multi-floor parking lot that allocates spots, tracks active parking tickets, and calculates checkout fees.

Run the demo from PowerShell:

```powershell
$files = Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName }
javac -d target/classes $files
java -cp target/classes org.parkinglot.ParkingLotApplication
```

## Data Model

In-memory repository in this implementation:

| Entity | Fields | Notes |
| --- | --- | --- |
| `ParkingSpot` | `spotId`, `floorNumber`, `size`, `isAvailable` | Spot availability changes on check-in and checkout. |
| `Vehicle` | `licensePlate`, `type` | Vehicle type maps to the minimum required spot size. |
| `ParkingTicket` | `ticketId`, `vehicle`, `spot`, `entryTime`, `exitTime`, `fee` | Active ticket exists from check-in until checkout. |

Main services:

| Method | Requirement Covered |
| --- | --- |
| `addParkingSpot` | Maintains parking inventory. |
| `checkIn` | Assigns a spot, records entry time, updates availability. |
| `checkOut` | Records exit time, calculates fee, frees the spot. |
| `getAvailableSpots` / `getAvailableSpotCount` | Real-time availability lookup. |
| `getActiveTicket` | Active parking transaction lookup. |

Relational schema equivalent:

```sql
CREATE TABLE parking_spots (
    spot_id VARCHAR(32) PRIMARY KEY,
    floor_number INT NOT NULL,
    size VARCHAR(16) NOT NULL,
    is_available BOOLEAN NOT NULL
);

CREATE TABLE vehicles (
    license_plate VARCHAR(32) PRIMARY KEY,
    vehicle_type VARCHAR(16) NOT NULL
);

CREATE TABLE parking_tickets (
    ticket_id VARCHAR(64) PRIMARY KEY,
    license_plate VARCHAR(32) NOT NULL,
    spot_id VARCHAR(32) NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP NULL,
    fee DECIMAL(10, 2) NULL,
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)
);
```

## Spot Allocation

`DefaultSpotAllocationStrategy` uses best fit:

1. Filter available spots that can fit the vehicle.
2. Prefer the smallest suitable spot size.
3. Then prefer lower floors and lexicographically smaller spot IDs.
4. Reserve the selected spot atomically through `ParkingSpot.reserve()`.

Size rules:

| Vehicle | Minimum Spot |
| --- | --- |
| Motorcycle | Small |
| Car | Medium |
| Bus | Large |

Larger spots can accept smaller vehicles when smaller spots are unavailable.

## Fee Calculation

`HourlyFeeCalculationStrategy` charges by started hour with a one-hour minimum:

| Vehicle | Rate |
| --- | --- |
| Motorcycle | Rs. 10/hour |
| Car | Rs. 20/hour |
| Bus | Rs. 50/hour |

## Concurrency

The repository uses concurrent collections. Check-in and checkout are synchronized at the manager level to keep ticket creation, duplicate active-vehicle checks, allocation, and release consistent.

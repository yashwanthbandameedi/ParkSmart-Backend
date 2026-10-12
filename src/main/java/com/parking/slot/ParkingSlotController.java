package com.parking.slot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin(origins = {"http://localhost:5173", "https://park-smart-frontend.vercel.app"})
public class ParkingSlotController {
    private final ParkingSlotRepository repository;
    public ParkingSlotController(ParkingSlotRepository repository) { this.repository = repository; }

    public record CreateLocationRequest(String locationName, String address, Double latitude, Double longitude, Double hourlyRate, Integer numberOfSlots, String hostEmail) {}
    public record SlotView(Long id, String slotNumber, boolean occupied, String locationName, String address, Double latitude, Double longitude, Double hourlyRate, String hostEmail, Double distanceKm) {}

    @GetMapping
    public List<ParkingSlot> getAllSlots() { return repository.findAll(); }

    @GetMapping("/host/{email}")
    public List<ParkingSlot> getHostSlots(@PathVariable String email) { return repository.findByHostEmailIgnoreCase(email); }

    @GetMapping("/nearby")
    public List<SlotView> nearby(@RequestParam double latitude, @RequestParam double longitude, @RequestParam(defaultValue = "10") double radiusKm) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180 || radiusKm <= 0 || radiusKm > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid location or search radius.");
        return repository.findAll().stream()
            .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
            .map(s -> new SlotView(s.getId(), s.getSlotNumber(), s.isOccupied(), s.getLocationName(), s.getAddress(), s.getLatitude(), s.getLongitude(), s.getHourlyRate(), s.getHostEmail(), distance(latitude, longitude, s.getLatitude(), s.getLongitude())))
            .filter(s -> s.distanceKm() <= radiusKm && !s.occupied())
            .sorted(Comparator.comparing(SlotView::distanceKm))
            .collect(Collectors.toList());
    }

    @PostMapping("/host")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ParkingSlot> createLocation(@RequestBody CreateLocationRequest request) {
        if (request.locationName() == null || request.locationName().isBlank() || request.address() == null || request.address().isBlank() || request.hostEmail() == null || request.hostEmail().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location name, address, and host email are required.");
        if (request.latitude() == null || request.longitude() == null || request.latitude() < -90 || request.latitude() > 90 || request.longitude() < -180 || request.longitude() > 180)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter valid latitude and longitude coordinates.");
        if (request.hourlyRate() == null || request.hourlyRate() < 0 || request.numberOfSlots() == null || request.numberOfSlots() < 1 || request.numberOfSlots() > 200)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rate must be 0 or more and slot count must be between 1 and 200.");
        List<ParkingSlot> created = new ArrayList<>();
        String prefix = request.locationName().trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (prefix.isBlank()) prefix = "LOT";
        prefix = prefix.substring(0, Math.min(prefix.length(), 5));
        for (int i = 1; i <= request.numberOfSlots(); i++) {
            created.add(new ParkingSlot(prefix + String.format("%02d", i), request.locationName().trim(), request.address().trim(), request.latitude(), request.longitude(), request.hourlyRate(), request.hostEmail().trim().toLowerCase()));
        }
        return repository.saveAll(created);
    }

    @PutMapping("/{id}")
    public ParkingSlot updateSlot(@PathVariable Long id, @RequestBody ParkingSlot input) {
        ParkingSlot existing = repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking slot not found."));
        if (input.getSlotNumber() != null && !input.getSlotNumber().isBlank()) existing.setSlotNumber(input.getSlotNumber());
        existing.setOccupied(input.isOccupied());
        return repository.save(existing);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1), dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadius * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}

package com.parking.slot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParkingSlotDataLoader implements CommandLineRunner {
    private final ParkingSlotRepository repository;
    public ParkingSlotDataLoader(ParkingSlotRepository repository) { this.repository = repository; }

    @Override
    public void run(String... args) {
        // Preserve existing rows and fill new location columns for the original demo slots.
        if (repository.count() == 0) {
            List<ParkingSlot> slots = new ArrayList<>();
            for (int i = 1; i <= 20; i++) slots.add(new ParkingSlot(String.format("P%02d", i), false));
            repository.saveAll(slots);
        } else {
            List<ParkingSlot> slots = repository.findAll();
            boolean changed = false;
            for (ParkingSlot slot : slots) {
                if (slot.getLocationName() == null || slot.getLocationName().isBlank()) {
                    slot.setLocationName("ParkSmart Demo Parking");
                    slot.setAddress("Hyderabad, Telangana (demo location)");
                    slot.setLatitude(17.3850);
                    slot.setLongitude(78.4867);
                    slot.setHourlyRate(30.0);
                    slot.setHostEmail("demo-host@parksmart.local");
                    changed = true;
                }
            }
            if (changed) repository.saveAll(slots);
        }
    }
}

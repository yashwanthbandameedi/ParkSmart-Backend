
package com.parking.booking;

import com.parking.slot.ParkingSlot;
import com.parking.slot.ParkingSlotRepository;
import com.parking.payment.HostPaymentSettings;
import com.parking.payment.HostPaymentSettingsRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = {"http://localhost:5173", "https://park-smart-frontend.vercel.app"})
public class BookingController {

    private final BookingRepository bookingRepository;
    private final ParkingSlotRepository slotRepository;
    private final HostPaymentSettingsRepository paymentSettingsRepository;

    public BookingController(
            BookingRepository bookingRepository,
            ParkingSlotRepository slotRepository,
            HostPaymentSettingsRepository paymentSettingsRepository) {

        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.paymentSettingsRepository = paymentSettingsRepository;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/user/{email}")
    public List<Booking> getUserBookings(@PathVariable String email) {
        return bookingRepository
                .findByUserEmailIgnoreCaseOrderByBookingDateDesc(email);
    }

    @GetMapping("/host/{email}")
    public List<Booking> getHostBookings(@PathVariable String email) {
        return bookingRepository
                .findByHostEmailIgnoreCaseOrderByBookingDateDesc(email);
    }

    @PostMapping("/{slotId}")
    @Transactional
    public BookingResponse createBooking(
            @PathVariable Long slotId,
            @RequestParam String userEmail) {

        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User email is required.");
        }

        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking slot not found."));

        if (slot.isOccupied()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Parking slot is already occupied.");
        }

        slot.setOccupied(true);
        slotRepository.save(slot);

        String bookingCode = "BK-" +
                UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase();

        Booking booking = bookingRepository.save(
                new Booking(
                        bookingCode,
                        slot.getId(),
                        slot.getSlotNumber(),
                        slot.getHostEmail(),
                        userEmail.trim().toLowerCase(),
                        LocalDateTime.now(),
                        "Confirmed"
                )
        );

        String qrImagePath = null;

        if (slot.getHostEmail() != null
                && !slot.getHostEmail().isBlank()) {

            qrImagePath = paymentSettingsRepository
                    .findByHostEmailIgnoreCase(slot.getHostEmail())
                    .map(HostPaymentSettings::getQrImagePath)
                    .orElse(null);
        }

        return new BookingResponse(
                booking,
                qrImagePath,
                slot.getHourlyRate()
        );
    }

    @PutMapping("/{bookingId}/verify-payment")
    @Transactional
    public Booking verifyPayment(
            @PathVariable Long bookingId,
            @RequestParam String hostEmail) {

        if (hostEmail == null || hostEmail.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Host email is required.");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found."));
                        System.out.println("Request hostEmail: " + hostEmail);
System.out.println("Booking hostEmail: " + booking.getHostEmail());

        if (booking.getHostEmail() == null
                || !booking.getHostEmail().equalsIgnoreCase(
                        hostEmail.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This booking does not belong to this host.");
        }

        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            return booking;
        }

        booking.setPaymentStatus("PAID");

        return bookingRepository.save(booking);
    }
}

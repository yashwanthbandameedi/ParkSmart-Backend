package com.parking.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmailIgnoreCaseOrderByBookingDateDesc(String userEmail);
    List<Booking> findByHostEmailIgnoreCaseOrderByBookingDateDesc(String hostEmail);
}

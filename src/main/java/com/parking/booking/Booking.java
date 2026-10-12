
package com.parking.booking;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingCode;

    private Long slotId;
    private String slotNumber;
    private String hostEmail;
    private String userEmail;
    private LocalDateTime bookingDate;
    private String status;

    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    public Booking() {
    }

    public Booking(
            String bookingCode,
            Long slotId,
            String slotNumber,
            String hostEmail,
            String userEmail,
            LocalDateTime bookingDate,
            String status) {

        this.bookingCode = bookingCode;
        this.slotId = slotId;
        this.slotNumber = slotNumber;
        this.hostEmail = hostEmail;
        this.userEmail = userEmail;
        this.bookingDate = bookingDate;
        this.status = status;
        this.paymentStatus = "PENDING";
    }

    public Long getId() {
        return id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public Long getSlotId() {
        return slotId;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}

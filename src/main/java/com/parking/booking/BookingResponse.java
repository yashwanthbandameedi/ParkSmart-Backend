
package com.parking.booking;

public class BookingResponse {

    private Long id;
    private String bookingCode;
    private Long slotId;
    private String slotNumber;
    private String hostEmail;
    private String userEmail;
    private String status;
    private String paymentStatus;
    private String qrImagePath;
    private Double hourlyRate;

    public BookingResponse(
            Booking booking,
            String qrImagePath,
            Double hourlyRate) {

        this.id = booking.getId();
        this.bookingCode = booking.getBookingCode();
        this.slotId = booking.getSlotId();
        this.slotNumber = booking.getSlotNumber();
        this.hostEmail = booking.getHostEmail();
        this.userEmail = booking.getUserEmail();
        this.status = booking.getStatus();
        this.paymentStatus = booking.getPaymentStatus();
        this.qrImagePath = qrImagePath;
        this.hourlyRate = hourlyRate;
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

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getQrImagePath() {
        return qrImagePath;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }
}

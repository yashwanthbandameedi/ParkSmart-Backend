package com.parking.slot;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_slot")
public class ParkingSlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String slotNumber;
    private boolean occupied;
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double hourlyRate;
    private String hostEmail;

    public ParkingSlot() {}
    public ParkingSlot(String slotNumber, boolean occupied) {
        this.slotNumber = slotNumber; this.occupied = occupied;
        this.locationName = "ParkSmart Demo Parking";
        this.address = "Hyderabad, Telangana (demo location)";
        this.latitude = 17.3850; this.longitude = 78.4867;
        this.hourlyRate = 30.0; this.hostEmail = "demo-host@parksmart.local";
    }
    public ParkingSlot(String slotNumber, String locationName, String address, Double latitude, Double longitude, Double hourlyRate, String hostEmail) {
        this.slotNumber = slotNumber; this.occupied = false; this.locationName = locationName; this.address = address;
        this.latitude = latitude; this.longitude = longitude; this.hourlyRate = hourlyRate; this.hostEmail = hostEmail;
    }
    public Long getId() { return id; }
    public String getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return occupied; }
    public String getLocationName() { return locationName; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getHourlyRate() { return hourlyRate; }
    public String getHostEmail() { return hostEmail; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }
    public void setHostEmail(String hostEmail) { this.hostEmail = hostEmail; }
}

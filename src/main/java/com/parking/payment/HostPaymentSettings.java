
package com.parking.payment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "host_payment_settings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_host_payment_email",
            columnNames = "host_email"
        )
    }
)
public class HostPaymentSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_email", nullable = false, unique = true)
    private String hostEmail;

    @Column(name = "qr_image_path", nullable = false, length = 500)
    private String qrImagePath;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public HostPaymentSettings() {
    }

    public HostPaymentSettings(String hostEmail, String qrImagePath) {
        this.hostEmail = hostEmail;
        this.qrImagePath = qrImagePath;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public void setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
    }

    public String getQrImagePath() {
        return qrImagePath;
    }

    public void setQrImagePath(String qrImagePath) {
        this.qrImagePath = qrImagePath;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

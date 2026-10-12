
package com.parking.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-settings")
@CrossOrigin(origins = "http://localhost:5173")
public class HostPaymentController {

    private final HostPaymentSettingsRepository repository;

    private final Path uploadDirectory =
            Paths.get("uploads", "payment-qr").toAbsolutePath().normalize();

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/png", "image/jpeg", "image/webp");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public HostPaymentController(HostPaymentSettingsRepository repository) {
        this.repository = repository;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, String> uploadQr(
            @RequestParam String hostEmail,
            @RequestParam("file") MultipartFile file) {

        if (hostEmail == null || hostEmail.isBlank()
                || !hostEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A valid host email is required.");
        }

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Please select a QR image.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "QR image must be 5 MB or smaller.");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PNG, JPEG, and WebP images are accepted.");
        }

        try {
            Files.createDirectories(uploadDirectory);

            String filename = UUID.randomUUID() + "."
                    + extensionFor(file.getContentType());

            Path destination = uploadDirectory.resolve(filename).normalize();

            if (!destination.startsWith(uploadDirectory)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid upload path.");
            }

            Files.copy(file.getInputStream(), destination,
                    StandardCopyOption.REPLACE_EXISTING);

            String relativePath = "/uploads/payment-qr/" + filename;

            HostPaymentSettings settings =
                    repository.findByHostEmailIgnoreCase(hostEmail.trim())
                            .orElseGet(HostPaymentSettings::new);

            settings.setHostEmail(hostEmail.trim().toLowerCase());
            settings.setQrImagePath(relativePath);
            repository.save(settings);

            return Map.of(
                    "message", "QR image uploaded successfully.",
                    "qrImagePath", relativePath
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save the QR image.");
        }
    }

    @GetMapping("/{hostEmail}")
    public HostPaymentSettings getPaymentSettings(
            @PathVariable String hostEmail) {

        return repository.findByHostEmailIgnoreCase(hostEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "This host has not uploaded a payment QR yet."));
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unsupported image type.");
        };
    }
}

package com.parking.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final UserAccountRepository repository;
    public AuthController(UserAccountRepository repository) { this.repository = repository; }

    public record RegisterRequest(String fullName, String email, String password, String role) {}
    public record LoginRequest(String email, String password) {}
    public record PublicUser(Long id, String fullName, String email, String role) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public PublicUser register(@RequestBody RegisterRequest request) {
        if (request.fullName() == null || request.fullName().isBlank() || request.email() == null || !request.email().contains("@") || request.password() == null || request.password().length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter a name, valid email, and password of at least 6 characters.");
        String role = request.role() == null ? "USER" : request.role().trim().toUpperCase();
        if (!role.equals("USER") && !role.equals("HOST")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be USER or HOST.");
        String email = request.email().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists. Please log in.");
        UserAccount saved = repository.save(new UserAccount(request.fullName().trim(), email, PasswordUtil.hash(request.password()), role));
        return publicUser(saved);
    }

    @PostMapping("/login")
    public PublicUser login(@RequestBody LoginRequest request) {
        if (request.email() == null || request.password() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enter your email and password.");
        UserAccount user = repository.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect."));
        if (!PasswordUtil.matches(request.password(), user.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or password is incorrect.");
        return publicUser(user);
    }
    private PublicUser publicUser(UserAccount user) { return new PublicUser(user.getId(), user.getFullName(), user.getEmail(), user.getRole()); }
}


package it.example.session_backend.controller;

import it.example.session_backend.model.User;
import it.example.session_backend.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(
                request.username(),
                request.password()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                    "id", user.getId(),
                    "username", user.getUsername()
                )
            );
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(
                Map.of("error", ex.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody RegisterRequest request,
            HttpSession session) {

        boolean valid = authService.verifyCredentials(
            request.username(),
            request.password()
        );

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Credenziali non valide")
            );
        }

        session.setAttribute("username", request.username());

        var authentication = new UsernamePasswordAuthenticationToken(
            request.username(),
            null,
            java.util.Collections.emptyList()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            context
        );

        return ResponseEntity.ok(
            Map.of(
                "message", "Login effettuato",
                "username", request.username()
            )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object username = session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Non autenticato")
            );
        }

        return ResponseEntity.ok(
            Map.of("username", username)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();

        return ResponseEntity.ok(
            Map.of("message", "Logout effettuato")
        );
    }

    public record RegisterRequest(String username, String password) {
    }
}

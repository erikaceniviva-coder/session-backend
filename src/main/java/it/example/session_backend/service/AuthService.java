
package it.example.session_backend.service;

import it.example.session_backend.model.User;
import it.example.session_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String username, String password) {
        if (username == null ||
                !username.matches("[a-zA-Z0-9_.-]{3,50}")) {
            throw new IllegalArgumentException(
                    "Username non valido: usa 3-50 lettere, numeri, punti, trattini o underscore."
            );
        }

        if (password == null ||
                password.length() < 12 ||
                password.length() > 72) {
            throw new IllegalArgumentException(
                    "La password deve contenere da 12 a 72 caratteri."
            );
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username già utilizzato."
            );
        }

        String passwordHash = passwordEncoder.encode(password);
        return userRepository.save(username, passwordHash);
    }

    public boolean verifyCredentials(
            String username,
            String password) {

        if (username == null || password == null) {
            return false;
        }

        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(
                        password,
                        user.getPasswordHash()
                ))
                .orElse(false);
    }
}



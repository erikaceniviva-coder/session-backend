
package it.example.session_backend.controller;

import it.example.session_backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
        session = new MockHttpSession();
    }

    @Test
    void loginConCredenzialiValideRestituisce200() {
        when(authService.verifyCredentials("portfolio_user", "Portfolio26!"))
            .thenReturn(true);

        var response = authController.login(
            new AuthController.RegisterRequest(
                "portfolio_user",
                "Portfolio26!"
            ),
            session
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
            "portfolio_user",
            session.getAttribute("username")
        );
    }

    @Test
    void loginConPasswordErrataRestituisce401() {
        when(authService.verifyCredentials("portfolio_user", "password-errata"))
            .thenReturn(false);

        var response = authController.login(
            new AuthController.RegisterRequest(
                "portfolio_user",
                "password-errata"
            ),
            session
        );

        assertEquals(
            HttpStatus.UNAUTHORIZED,
            response.getStatusCode()
        );
        assertNull(session.getAttribute("username"));
    }

    @Test
    void meSenzaUtenteRestituisce401() {
        var response = authController.me(session);

        assertEquals(
            HttpStatus.UNAUTHORIZED,
            response.getStatusCode()
        );
    }

    @Test
    void meConUtenteInSessioneRestituisce200() {
        session.setAttribute("username", "portfolio_user");

        var response = authController.me(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void logoutInvalidaLaSessione() {
        session.setAttribute("username", "portfolio_user");

        var response = authController.logout(session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThrows(
            IllegalStateException.class,
            () -> session.getAttribute("username")
        );
    }
}

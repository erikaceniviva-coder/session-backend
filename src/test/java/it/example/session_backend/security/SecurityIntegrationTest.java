
package it.example.session_backend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HttpClient createClient(CookieManager cookies) {
        return HttpClient.newBuilder()
                .cookieHandler(cookies)
                .build();
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    private HttpResponse<String> postJson(
            HttpClient client,
            String path,
            String json) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    @Test
    void endpointMeSenzaAutenticazioneRestituisce401() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/api/auth/me"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("Non autenticato"));
    }

    @Test
    void registrazioneLoginMeELogoutFunzionano() throws Exception {
        CookieManager cookies = new CookieManager();
        cookies.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient client = createClient(cookies);

        String username = "test_" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String password = "PasswordTest123!";

        String registrationJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "username", username,
                        "password", password
                )
        );

        HttpResponse<String> registration = postJson(
                client,
                "/api/auth/register",
                registrationJson
        );

        assertEquals(201, registration.statusCode());

        String loginJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "username", username,
                        "password", password
                )
        );

        HttpResponse<String> login = postJson(
                client,
                "/api/auth/login",
                loginJson
        );

        assertEquals(200, login.statusCode());
        assertTrue(login.body().contains("Login effettuato"));

        HttpRequest meRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/api/auth/me"))
                .GET()
                .build();

        HttpResponse<String> me = client.send(
                meRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, me.statusCode());
        JsonNode meJson = objectMapper.readTree(me.body());
        assertEquals(username, meJson.get("username").asText());

        HttpResponse<String> logout = postJson(
                client,
                "/api/auth/logout",
                "{}"
        );

        assertEquals(200, logout.statusCode());

        HttpResponse<String> meAfterLogout = client.send(
                meRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(401, meAfterLogout.statusCode());
    }
}

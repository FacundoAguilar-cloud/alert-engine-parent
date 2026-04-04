package saas.app.engine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthService {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String auth0Domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.jwt.audience}")
    private String audience;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {
        try {
            String tokenUrl = auth0Domain + "oauth/token";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("grant_type", "authorization_code");
            requestBody.put("client_id", clientId);
            requestBody.put("client_secret", clientSecret);
            requestBody.put("code", code);
            requestBody.put("redirect_uri", redirectUri);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> tokenResponse = response.getBody();
                log.info("Token obtenido exitosamente para el código de autorización");
                return tokenResponse;
            } else {
                log.error("Error al obtener token: {}", response.getStatusCode());
                throw new RuntimeException("Error al obtener token de Auth0");
            }

        } catch (Exception e) {
            log.error("Error en el intercambio de código por token: {}", e.getMessage());
            throw new RuntimeException("Error al intercambiar código por token: " + e.getMessage());
        }
    }

    public Map<String, Object> getUserInfo(String accessToken) {
        try {
            String userInfoUrl = auth0Domain + "userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                log.error("Error al obtener userinfo: {}", response.getStatusCode());
                throw new RuntimeException("Error al obtener información del usuario");
            }

        } catch (Exception e) {
            log.error("Error al obtener userinfo: {}", e.getMessage());
            throw new RuntimeException("Error al obtener información del usuario: " + e.getMessage());
        }
    }

    public String buildLoginUrl() {
        String authUrl = auth0Domain + "authorize?" +
            "response_type=code" +
            "&client_id=" + clientId +
            "&redirect_uri=" + "http://localhost:8080/api/auth/callback" +
            "&audience=" + audience +
            "&scope=openid profile email";
        
        return authUrl;
    }
}
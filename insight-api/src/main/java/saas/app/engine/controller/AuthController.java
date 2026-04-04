package saas.app.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saas.app.engine.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        String loginUrl = authService.buildLoginUrl();
        log.info("Redirigiendo a Auth0 para autenticación");
        
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", loginUrl)
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(@RequestParam String code) {
        try {
            log.info("Recibiendo código de autorización de Auth0");
            
            String redirectUri = "http://localhost:8080/api/auth/callback";
            Map<String, Object> tokenResponse = authService.exchangeCodeForToken(code, redirectUri);
            
            String accessToken = (String) tokenResponse.get("access_token");
            Map<String, Object> userInfo = authService.getUserInfo(accessToken);
            
            log.info("Usuario autenticado exitosamente: {}", userInfo.get("email"));
            
            return ResponseEntity.ok(Map.of(
                "access_token", accessToken,
                "token_type", tokenResponse.get("token_type"),
                "expires_in", tokenResponse.get("expires_in"),
                "user", Map.of(
                    "sub", userInfo.get("sub"),
                    "email", userInfo.get("email"),
                    "name", userInfo.get("name") != null ? userInfo.get("name") : "",
                    "picture", userInfo.get("picture") != null ? userInfo.get("picture") : ""
                )
            ));
            
        } catch (Exception e) {
            log.error("Error en el callback de Auth0: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Error en autenticación: " + e.getMessage()));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        String logoutUrl = "https://dev-6p5syw5vn131xu8k.us.auth0.com/v2/logout?" +
                "client_id=M25mEZO5kThSSUQfKy7bYcWskCt3NIMu&" +
                "returnTo=http://localhost:8080";
        
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", logoutUrl)
                .build();
    }
}
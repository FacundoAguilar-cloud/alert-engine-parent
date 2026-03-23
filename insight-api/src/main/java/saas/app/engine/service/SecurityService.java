package saas.app.engine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

@Service
@Slf4j
public class SecurityService {
    //ver si mas tarde hay que cambiar algo o simplemente añadir una mejora.
    public String getCurrentUserAuth0Subject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("No autenticado.");
        }
        return jwt.getSubject();
    }

    public String getCurrentUserEmail(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("No autenticado.");
        }
        String email = jwt.getClaimAsString("email");
        return email != null ? email : "Sin email";
    }

    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("No autenticado.");
        }
        String name = jwt.getClaimAsString("name");
        return name != null ? name : "Sin nombre";
    }
}

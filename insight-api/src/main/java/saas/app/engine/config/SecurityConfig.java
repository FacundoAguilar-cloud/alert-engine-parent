package saas.app.engine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf( csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
                .authorizeHttpRequests(auth -> auth
                        //publico
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        //privado
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));


            return http.build();
    }
}

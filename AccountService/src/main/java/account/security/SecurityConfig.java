package account.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())

            // ✅ Authorize requests
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/actuator/**").permitAll() // allow public paths
                .requestMatchers(HttpMethod.GET, "/api/accounts/**").authenticated() // all GET under /api/accounts/**
                .requestMatchers(HttpMethod.PUT, "/api/accounts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/accounts/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/accounts/**").authenticated()
                .anyRequest().authenticated()
            )

            // ✅ Add JWT filter before username/password filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
package pe.edu.upc.iot_service.iot.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.upc.iot_service.iot.domain.model.services.ApiKeyService;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;

/**
 * Security configuration for the IoT service.
 *
 * Three authentication paths:
 *   1. Device endpoints (POST /hydration, POST /weight) → validated by ApiKeyAuthenticationFilter
 *   2. Management endpoints (GET queries, POST /devices) → validated by JWT Bearer (OAuth2 RS)
 *   3. Public endpoints (Swagger, actuator, device registration) → permit all
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final IotDeviceRepository deviceRepository;
    private final ApiKeyService       apiKeyService;

    public WebSecurityConfiguration(IotDeviceRepository deviceRepository,
                                    ApiKeyService apiKeyService) {
        this.deviceRepository = deviceRepository;
        this.apiKeyService    = apiKeyService;
    }

    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
        return new ApiKeyAuthenticationFilter(deviceRepository, apiKeyService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger / actuator — public
                .requestMatchers(
                    "/api/v1/iot/devices",          // POST register device (user is auth'd via JWT)
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**"
                ).permitAll()
                // Device ingestion endpoints — authenticated by ApiKeyAuthenticationFilter
                .requestMatchers(HttpMethod.POST, "/api/v1/iot/hydration").hasRole("DEVICE")
                .requestMatchers(HttpMethod.POST, "/api/v1/iot/weight").hasRole("DEVICE")
                // All other endpoints require a valid JWT
                .anyRequest().authenticated()
            )
            // JWT validation via the IAM service JWKS endpoint
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {}))
            // API Key filter runs before the standard auth filter
            .addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

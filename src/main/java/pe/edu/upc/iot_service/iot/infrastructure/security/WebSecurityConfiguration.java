package pe.edu.upc.iot_service.iot.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.edu.upc.iot_service.iot.domain.model.services.ApiKeyService;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;

import java.util.function.Supplier;

/**
 * Security configuration for the IoT service.
 *
 * Three authentication paths:
 *   1. Device ingestion endpoints (POST /hydration, POST /weight) validated by ApiKeyAuthenticationFilter.
 *   2. Management endpoints validated by JWT Bearer (OAuth2 Resource Server).
 *   3. Public endpoints limited to Swagger, actuator and error handling.
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

  /*  @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${legacy.jwt.issuer:iam-service}") String legacyJwtIssuer
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger / actuator — public
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/actuator/**",
                    "/error"
                ).permitAll()

                // Device ingestion endpoints — authenticated by ApiKeyAuthenticationFilter
                .requestMatchers(HttpMethod.POST, "/api/v1/iot/hydration").hasRole("DEVICE")
                .requestMatchers(HttpMethod.POST, "/api/v1/iot/weight").hasRole("DEVICE")
                // Management/query endpoints — authenticated by JWT Bearer
                .requestMatchers(HttpMethod.GET, "/api/v1/iot/**").access((authentication, context) ->
                        hasPermissionOrLegacyJwt(authentication, context, legacyJwtIssuer, "read:iot"))
                .requestMatchers(HttpMethod.HEAD, "/api/v1/iot/**").access((authentication, context) ->
                        hasPermissionOrLegacyJwt(authentication, context, legacyJwtIssuer, "read:iot"))
                .anyRequest().access((authentication, context) ->
                        hasPermissionOrLegacyJwt(authentication, context, legacyJwtIssuer, "write:iot"))
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                    jwtConfigurer.jwtAuthenticationConverter(IotJwtAuthenticationConverter.jwtAuthenticationConverter())))
            // API Key filter runs before the standard auth filter
            .addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    /*

   */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / actuator — public
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/**",
                                "/error"

                        ).permitAll()
                        .anyRequest().permitAll()
                );
        //    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
        //      jwtConfigurer.jwtAuthenticationConverter(IotJwtAuthenticationConverter.jwtAuthenticationConverter())))
        // API Key filter runs before the standard auth filter
        //      .addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private AuthorizationDecision hasPermissionOrLegacyJwt(
            Supplier<Authentication> authenticationSupplier,
            RequestAuthorizationContext context,
            String legacyJwtIssuer,
            String permission) {
        var authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(
                hasAuthority(authentication, permission)
                        || hasAuthority(authentication, "ROLE_SERVICE")
                        || isLegacyJwt(authentication, legacyJwtIssuer));
    }

    private boolean hasAuthority(Authentication authentication, String expectedAuthority) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> expectedAuthority.equals(authority.getAuthority()));
    }

    private boolean isLegacyJwt(Authentication authentication, String legacyJwtIssuer) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return false;
        }
        var issuer = jwtAuthentication.getToken().getIssuer();
        return issuer != null && legacyJwtIssuer.equals(issuer.toString());
    }
}

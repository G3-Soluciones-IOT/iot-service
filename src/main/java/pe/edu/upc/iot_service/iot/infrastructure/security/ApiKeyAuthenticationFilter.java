package pe.edu.upc.iot_service.iot.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pe.edu.upc.iot_service.iot.domain.model.services.ApiKeyService;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;

import java.io.IOException;
import java.util.Collections;

/**
 * Intercepts requests to /api/v1/iot/hydration and /api/v1/iot/weight.
 * Validates the X-API-Key header against the stored BCrypt hash in iot_devices.
 * If valid, sets a ROLE_DEVICE authentication in the SecurityContext.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";

    private final IotDeviceRepository deviceRepository;
    private final ApiKeyService       apiKeyService;

    public ApiKeyAuthenticationFilter(IotDeviceRepository deviceRepository,
                                      ApiKeyService apiKeyService) {
        this.deviceRepository = deviceRepository;
        this.apiKeyService    = apiKeyService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/v1/iot/hydration") && request.getMethod().equals("POST"))
                && !(path.startsWith("/api/v1/iot/weight")     && request.getMethod().equals("POST"));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String rawKey = request.getHeader(API_KEY_HEADER);

        if (rawKey == null || rawKey.isBlank()) {
            LOG.warn("Missing X-API-Key header on device endpoint {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing X-API-Key header");
            return;
        }

        boolean authenticated = deviceRepository.findAll().stream()
                .anyMatch(d -> d.isActive() && apiKeyService.matches(rawKey, d.getApiKeyHash()));

        if (!authenticated) {
            LOG.warn("Invalid API Key received for path {}", request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }

        var auth = new UsernamePasswordAuthenticationToken(
                "iot-device", null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEVICE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
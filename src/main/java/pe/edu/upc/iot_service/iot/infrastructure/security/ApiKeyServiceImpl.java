package pe.edu.upc.iot_service.iot.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.services.ApiKeyService;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates and validates IoT device API keys.
 * Raw keys are never stored — only their BCrypt hash is persisted.
 * The raw key is returned exactly once at device registration time.
 */
@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final BCryptPasswordEncoder encoder;

    public ApiKeyServiceImpl(@Value("${iot.api-key.salt:12}") int strength) {
        this.encoder = new BCryptPasswordEncoder(strength);
    }

    @Override
    public String generate() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        // Prefix "jf-sk-" makes it easy to identify JameoFit device keys
        return "jf-sk-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public String hash(String rawKey) {
        return encoder.encode(rawKey);
    }

    @Override
    public boolean matches(String rawKey, String hashedKey) {
        return encoder.matches(rawKey, hashedKey);
    }
}

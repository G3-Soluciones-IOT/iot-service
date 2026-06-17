package pe.edu.upc.iot_service.iot.domain.model.services;

public interface ApiKeyService {
    String generate();
    String hash(String rawKey);
    boolean matches(String rawKey, String hashedKey);
}

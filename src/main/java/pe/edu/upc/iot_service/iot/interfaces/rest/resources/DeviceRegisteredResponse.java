package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.*;
import java.time.Instant;
public record DeviceRegisteredResponse(String deviceId, Long userId,
        DeviceType deviceType, String apiKey, DeviceStatus status, Instant registeredAt) {}

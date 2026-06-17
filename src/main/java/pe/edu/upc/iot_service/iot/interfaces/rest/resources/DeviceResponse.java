package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.*;
import java.time.Instant;
public record DeviceResponse(String deviceId, Long userId, DeviceType deviceType,
        DeviceStatus status, Instant registeredAt, Instant lastSeenAt) {}

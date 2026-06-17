package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceType;
public record RegisterDeviceResource(
        @NotNull @Positive Long userId, @NotBlank String deviceId,
        @NotNull DeviceType deviceType) {}

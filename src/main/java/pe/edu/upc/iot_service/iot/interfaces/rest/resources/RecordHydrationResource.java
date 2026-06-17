package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import jakarta.validation.constraints.*;
public record RecordHydrationResource(
        @NotNull @Positive Long userId, @NotBlank String deviceId,
        @NotNull @Positive Float ml, @NotNull @Positive Float totalMl,
        @NotNull @Positive Float goalMl, Long ts) {}

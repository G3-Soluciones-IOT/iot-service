package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import jakarta.validation.constraints.*;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.MeasurementType;
public record RecordWeightResource(
        @NotNull @Positive Long userId, @NotBlank String deviceId,
        @NotNull @Positive Float grams, @NotNull MeasurementType measurementType, Long ts) {}

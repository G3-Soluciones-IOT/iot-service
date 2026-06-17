package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.MeasurementType;
import java.time.Instant;
public record WeightRecordResponse(Long id, Long userId, String deviceId,
        Float grams, MeasurementType measurementType, Instant recordedAt) {}

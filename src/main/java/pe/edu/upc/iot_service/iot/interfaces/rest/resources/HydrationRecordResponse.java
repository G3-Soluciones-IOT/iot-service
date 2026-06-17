package pe.edu.upc.iot_service.iot.interfaces.rest.resources;
import java.time.Instant;
public record HydrationRecordResponse(Long id, Long userId, String deviceId,
        Float amountMl, Float totalMl, Float goalMl,
        Float progressPercentage, Boolean goalReached, Instant recordedAt) {}

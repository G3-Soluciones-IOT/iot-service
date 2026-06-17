package pe.edu.upc.iot_service.iot.domain.model.commands;

public record RecordHydrationCommand(
        Long   userId,
        String deviceId,
        Float  ml,
        Float  totalMl,
        Float  goalMl,
        Long   ts) {}

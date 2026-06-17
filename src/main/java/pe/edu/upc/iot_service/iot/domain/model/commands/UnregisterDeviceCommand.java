package pe.edu.upc.iot_service.iot.domain.model.commands;

public record UnregisterDeviceCommand(Long userId, String deviceId) {}

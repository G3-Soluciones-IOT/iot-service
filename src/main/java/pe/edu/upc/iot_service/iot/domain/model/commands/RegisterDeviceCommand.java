package pe.edu.upc.iot_service.iot.domain.model.commands;

import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceType;

public record RegisterDeviceCommand(
        Long       userId,
        String     deviceId,
        DeviceType deviceType) {}

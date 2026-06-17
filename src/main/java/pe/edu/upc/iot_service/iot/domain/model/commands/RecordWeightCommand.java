package pe.edu.upc.iot_service.iot.domain.model.commands;

import pe.edu.upc.iot_service.iot.domain.model.valueobjects.MeasurementType;

public record RecordWeightCommand(
        Long            userId,
        String          deviceId,
        Float           grams,
        MeasurementType measurementType,
        Long            ts) {}

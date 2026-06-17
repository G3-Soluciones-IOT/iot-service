package pe.edu.upc.iot_service.iot.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordWeightCommand;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceInactiveException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceNotFoundException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.UnauthorizedDeviceException;
import pe.edu.upc.iot_service.iot.domain.model.services.*;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.WeightValue;

import java.time.Instant;

@Service
public class WeightCommandServiceImpl implements WeightCommandService {

    private final WeightRecordRepository weightRepository;
    private final IotDeviceRepository    deviceRepository;

    public WeightCommandServiceImpl(WeightRecordRepository weightRepository,
                                    IotDeviceRepository deviceRepository) {
        this.weightRepository = weightRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public WeightRecord handle(RecordWeightCommand command) {
        var device = deviceRepository.findByDeviceId(command.deviceId())
                .orElseThrow(() -> new DeviceNotFoundException(command.deviceId()));

        if (!device.isActive())
            throw new DeviceInactiveException(command.deviceId());

        if (!device.getUserId().value().equals(command.userId()))
            throw new UnauthorizedDeviceException();

        device.updateLastSeen();
        deviceRepository.save(device);

        var record = new WeightRecord(
                new UserId(command.userId()),
                new DeviceId(command.deviceId()),
                new WeightValue(command.grams()),
                command.measurementType(),
                command.ts() != null ? Instant.ofEpochMilli(command.ts()) : Instant.now()
        );

        return weightRepository.save(record);
    }
}

package pe.edu.upc.iot_service.iot.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordHydrationCommand;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceInactiveException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceNotFoundException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.UnauthorizedDeviceException;
import pe.edu.upc.iot_service.iot.domain.model.services.*;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.HydrationAmount;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;

import java.time.Instant;

@Service
public class HydrationCommandServiceImpl implements HydrationCommandService {

    private final HydrationRecordRepository hydrationRepository;
    private final IotDeviceRepository       deviceRepository;
    private final ApiKeyService             apiKeyService;

    public HydrationCommandServiceImpl(HydrationRecordRepository hydrationRepository,
                                       IotDeviceRepository deviceRepository,
                                       ApiKeyService apiKeyService) {
        this.hydrationRepository = hydrationRepository;
        this.deviceRepository    = deviceRepository;
        this.apiKeyService       = apiKeyService;
    }

    @Override
    public HydrationRecord handle(RecordHydrationCommand command) {
        var device = deviceRepository.findByDeviceId(command.deviceId())
                .orElseThrow(() -> new DeviceNotFoundException(command.deviceId()));

        if (!device.isActive())
            throw new DeviceInactiveException(command.deviceId());

        if (!device.getUserId().value().equals(command.userId()))
            throw new UnauthorizedDeviceException();

        device.updateLastSeen();
        deviceRepository.save(device);

        var record = new HydrationRecord(
                new UserId(command.userId()),
                new DeviceId(command.deviceId()),
                new HydrationAmount(command.ml()),
                command.totalMl(),
                command.goalMl(),
                command.ts() != null ? Instant.ofEpochMilli(command.ts()) : Instant.now()
        );

        return hydrationRepository.save(record);
    }
}

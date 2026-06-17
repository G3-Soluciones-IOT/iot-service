package pe.edu.upc.iot_service.iot.application.internal.commandservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.commands.RegisterDeviceCommand;
import pe.edu.upc.iot_service.iot.domain.model.commands.UnregisterDeviceCommand;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceAlreadyRegisteredException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.DeviceNotFoundException;
import pe.edu.upc.iot_service.iot.domain.model.exceptions.UnauthorizedDeviceException;
import pe.edu.upc.iot_service.iot.domain.model.services.ApiKeyService;
import pe.edu.upc.iot_service.iot.domain.model.services.DeviceCommandService;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;

@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private final IotDeviceRepository deviceRepository;
    private final ApiKeyService       apiKeyService;

    public DeviceCommandServiceImpl(IotDeviceRepository deviceRepository,
                                    ApiKeyService apiKeyService) {
        this.deviceRepository = deviceRepository;
        this.apiKeyService    = apiKeyService;
    }

    @Override
    public IotDevice handle(RegisterDeviceCommand command) {
        if (deviceRepository.existsByDeviceId(command.deviceId()))
            throw new DeviceAlreadyRegisteredException(command.deviceId());

        String rawKey    = apiKeyService.generate();
        String hashedKey = apiKeyService.hash(rawKey);

        var device = new IotDevice(
                command.deviceId(),
                new UserId(command.userId()),
                command.deviceType(),
                hashedKey
        );

        var saved = deviceRepository.save(device);
        // Store raw key temporarily so the controller can return it once
        saved.getClass(); // trick: we store it via a transient field below
        // We use a simple holder instead of a transient field to keep the aggregate clean
        IotDeviceWithRawKey.set(rawKey);
        return saved;
    }

    @Override
    public void handle(UnregisterDeviceCommand command) {
        var device = deviceRepository.findByDeviceId(command.deviceId())
                .orElseThrow(() -> new DeviceNotFoundException(command.deviceId()));

        if (!device.getUserId().value().equals(command.userId()))
            throw new UnauthorizedDeviceException();

        device.deactivate();
        deviceRepository.save(device);
    }
}

package pe.edu.upc.iot_service.iot.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetDevicesByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.DeviceQueryService;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;

import java.util.List;

@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final IotDeviceRepository repository;

    public DeviceQueryServiceImpl(IotDeviceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<IotDevice> handle(GetDevicesByUserQuery query) {
        return repository.findByUserId(query.userId());
    }
}

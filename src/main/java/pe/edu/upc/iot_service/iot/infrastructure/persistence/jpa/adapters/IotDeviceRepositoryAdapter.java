package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.adapters;

import org.springframework.stereotype.Component;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.services.IotDeviceRepository;
import pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories.JpaIotDeviceRepository;

import java.util.List;
import java.util.Optional;

@Component
public class IotDeviceRepositoryAdapter implements IotDeviceRepository {

    private final JpaIotDeviceRepository jpa;

    public IotDeviceRepositoryAdapter(JpaIotDeviceRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public IotDevice save(IotDevice device) {
        return jpa.save(device);
    }

    @Override
    public Optional<IotDevice> findByDeviceId(String deviceId) {
        return jpa.findByDeviceId(deviceId);
    }

    @Override
    public List<IotDevice> findByUserId(Long userId) {
        return jpa.findByUserId(userId);
    }

    @Override
    public boolean existsByDeviceId(String deviceId) {
        return jpa.existsByDeviceId(deviceId);
    }

    @Override
    public List<IotDevice> findAll() {
        return jpa.findAll();
    }
}
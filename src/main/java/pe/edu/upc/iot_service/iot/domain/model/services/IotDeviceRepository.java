package pe.edu.upc.iot_service.iot.domain.model.services;

import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;

import java.util.List;
import java.util.Optional;

public interface IotDeviceRepository {
    IotDevice save(IotDevice device);
    Optional<IotDevice> findByDeviceId(String deviceId);
    List<IotDevice> findByUserId(Long userId);
    boolean existsByDeviceId(String deviceId);
    List<IotDevice> findAll();
}

package pe.edu.upc.iot_service.iot.domain.model.services;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.commands.RegisterDeviceCommand;
import pe.edu.upc.iot_service.iot.domain.model.commands.UnregisterDeviceCommand;
public interface DeviceCommandService {
    IotDevice handle(RegisterDeviceCommand command);
    void handle(UnregisterDeviceCommand command);
}

package pe.edu.upc.iot_service.iot.interfaces.rest.transform;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.commands.RegisterDeviceCommand;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.*;
public class DeviceAssembler {
    public static RegisterDeviceCommand toCommand(RegisterDeviceResource r) {
        return new RegisterDeviceCommand(r.userId(), r.deviceId(), r.deviceType());
    }
    public static DeviceRegisteredResponse toRegisteredResponse(IotDevice e, String rawApiKey) {
        return new DeviceRegisteredResponse(e.getDeviceId(), e.getUserId().value(),
                e.getDeviceType(), rawApiKey, e.getStatus(), e.getRegisteredAt());
    }
    public static DeviceResponse toResponse(IotDevice e) {
        return new DeviceResponse(e.getDeviceId(), e.getUserId().value(),
                e.getDeviceType(), e.getStatus(), e.getRegisteredAt(), e.getLastSeenAt());
    }
}

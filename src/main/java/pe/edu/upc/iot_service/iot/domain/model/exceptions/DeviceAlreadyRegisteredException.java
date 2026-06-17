package pe.edu.upc.iot_service.iot.domain.model.exceptions;
public class DeviceAlreadyRegisteredException extends RuntimeException {
    public DeviceAlreadyRegisteredException(String deviceId) { super("Device already registered: " + deviceId); }
}

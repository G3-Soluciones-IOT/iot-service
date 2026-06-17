package pe.edu.upc.iot_service.iot.domain.model.exceptions;
public class DeviceInactiveException extends RuntimeException {
    public DeviceInactiveException(String deviceId) { super("Device is inactive: " + deviceId); }
}

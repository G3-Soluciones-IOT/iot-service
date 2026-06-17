package pe.edu.upc.iot_service.iot.domain.model.exceptions;
public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String deviceId) { super("Device not found: " + deviceId); }
}

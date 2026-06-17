package pe.edu.upc.iot_service.iot.domain.model.exceptions;
public class UnauthorizedDeviceException extends RuntimeException {
    public UnauthorizedDeviceException() { super("Device is not authorized to send data for this user"); }
}

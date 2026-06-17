package pe.edu.upc.iot_service.iot.application.internal.commandservices;

/**
 * Thread-local holder to pass the raw API Key from DeviceCommandServiceImpl
 * to the controller without polluting the aggregate root.
 * The controller must call consume() immediately after handle() and clear it.
 */
public final class IotDeviceWithRawKey {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private IotDeviceWithRawKey() {}

    public static void set(String rawKey) {
        HOLDER.set(rawKey);
    }

    public static String consume() {
        String key = HOLDER.get();
        HOLDER.remove();
        return key;
    }
}

package pe.edu.upc.iot_service.iot.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.iot_service.iot.application.internal.commandservices.IotDeviceWithRawKey;
import pe.edu.upc.iot_service.iot.domain.model.commands.UnregisterDeviceCommand;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetDevicesByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.DeviceCommandService;
import pe.edu.upc.iot_service.iot.domain.model.services.DeviceQueryService;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.DeviceRegisteredResponse;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.DeviceResponse;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.RegisterDeviceResource;
import pe.edu.upc.iot_service.iot.interfaces.rest.transform.DeviceAssembler;

import java.util.List;

/**
 * Manages IoT device registration and listing.
 * All endpoints require a valid JWT (the user registers their own device).
 *
 * POST /api/v1/iot/devices           → register a new device, returns raw API Key ONCE
 * GET  /api/v1/iot/devices/{userId}  → list devices linked to a user
 * DELETE /api/v1/iot/devices/{userId}/{deviceId} → unregister a device
 */
@RestController
@RequestMapping(value = "/api/v1/iot/devices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Devices", description = "IoT device registration and management")
public class DeviceController {

    private final DeviceCommandService commandService;
    private final DeviceQueryService   queryService;

    public DeviceController(DeviceCommandService commandService,
                             DeviceQueryService queryService) {
        this.commandService = commandService;
        this.queryService   = queryService;
    }

    @Operation(
        summary     = "Register a new IoT device",
        description = "Links a physical device to a user account and generates a one-time API Key. " +
                      "Store the returned apiKey in the ESP32 NVS/EEPROM — it will not be shown again.",
        security    = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<DeviceRegisteredResponse> register(
            @Valid @RequestBody RegisterDeviceResource resource) {

        var command = DeviceAssembler.toCommand(resource);
        var device  = commandService.handle(command);
        // Consume the raw API Key stored by the command service in the thread-local holder
        String rawKey  = IotDeviceWithRawKey.consume();
        var response   = DeviceAssembler.toRegisteredResponse(device, rawKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary     = "List devices for a user",
        description = "Returns all IoT devices (active and inactive) linked to the given userId.",
        security    = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}")
    public ResponseEntity<List<DeviceResponse>> getDevices(@PathVariable Long userId) {
        var query   = new GetDevicesByUserQuery(userId);
        var devices = queryService.handle(query);
        var response = devices.stream()
                .map(DeviceAssembler::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary     = "Unregister an IoT device",
        description = "Deactivates the device and revokes its API Key. The device will no longer " +
                      "be able to send data until re-registered.",
        security    = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{userId}/{deviceId}")
    public ResponseEntity<Void> unregister(
            @PathVariable Long   userId,
            @PathVariable String deviceId) {

        var command = new UnregisterDeviceCommand(userId, deviceId);
        commandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}

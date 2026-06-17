package pe.edu.upc.iot_service.iot.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceStatus;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceType;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;
import pe.edu.upc.iot_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.Instant;

@Entity
@Table(name = "iot_devices",
       uniqueConstraints = @UniqueConstraint(columnNames = "device_id"))
public class IotDevice extends AuditableAbstractAggregateRoot<IotDevice> {

    @Getter
    @Column(name = "device_id", nullable = false, unique = true, length = 64)
    private String deviceId;

    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 20)
    private DeviceType deviceType;

    @Getter
    @Column(name = "api_key_hash", nullable = false)
    private String apiKeyHash;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private DeviceStatus status;

    @Getter
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @Getter
    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    protected IotDevice() {}

    public IotDevice(String deviceId, UserId userId, DeviceType deviceType, String apiKeyHash) {
        this.deviceId     = deviceId;
        this.userId       = userId;
        this.deviceType   = deviceType;
        this.apiKeyHash   = apiKeyHash;
        this.status       = DeviceStatus.ACTIVE;
        this.registeredAt = Instant.now();
    }

    public void deactivate() {
        this.status = DeviceStatus.INACTIVE;
    }

    public void updateLastSeen() {
        this.lastSeenAt = Instant.now();
    }

    public boolean isActive() {
        return DeviceStatus.ACTIVE.equals(this.status);
    }
}

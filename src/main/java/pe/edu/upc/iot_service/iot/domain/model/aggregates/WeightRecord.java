package pe.edu.upc.iot_service.iot.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.MeasurementType;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.WeightValue;
import pe.edu.upc.iot_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.Instant;

@Entity
@Table(name = "weight_records")
public class WeightRecord extends AuditableAbstractAggregateRoot<WeightRecord> {

    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id", nullable = false))
    private UserId userId;

    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "device_id", nullable = false, length = 64))
    private DeviceId deviceId;

    @Getter
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "grams", nullable = false))
    private WeightValue grams;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_type", nullable = false, length = 20)
    private MeasurementType measurementType;

    @Getter
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected WeightRecord() {}

    public WeightRecord(UserId userId, DeviceId deviceId,
                        WeightValue grams, MeasurementType measurementType,
                        Instant recordedAt) {
        this.userId          = userId;
        this.deviceId        = deviceId;
        this.grams           = grams;
        this.measurementType = measurementType;
        this.recordedAt      = recordedAt;
        validate();
    }

    private void validate() {
        if (measurementType == null)
            throw new IllegalArgumentException("measurementType must not be null");
        if (recordedAt == null)
            throw new IllegalArgumentException("recordedAt must not be null");
    }
}

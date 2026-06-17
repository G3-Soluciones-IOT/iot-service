package pe.edu.upc.iot_service.iot.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.DeviceId;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.HydrationAmount;
import pe.edu.upc.iot_service.iot.domain.model.valueobjects.UserId;
import pe.edu.upc.iot_service.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.time.Instant;

@Entity
@Table(name = "hydration_records")
public class HydrationRecord extends AuditableAbstractAggregateRoot<HydrationRecord> {

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
    @AttributeOverride(name = "value", column = @Column(name = "amount_ml", nullable = false))
    private HydrationAmount amountMl;

    @Getter
    @Column(name = "total_ml", nullable = false)
    private Float totalMl;

    @Getter
    @Column(name = "goal_ml", nullable = false)
    private Float goalMl;

    @Getter
    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected HydrationRecord() {}

    public HydrationRecord(UserId userId, DeviceId deviceId,
                           HydrationAmount amountMl, Float totalMl,
                           Float goalMl, Instant recordedAt) {
        this.userId     = userId;
        this.deviceId   = deviceId;
        this.amountMl   = amountMl;
        this.totalMl    = totalMl;
        this.goalMl     = goalMl;
        this.recordedAt = recordedAt;
        validate();
    }

    private void validate() {
        if (totalMl == null || totalMl < 0)
            throw new IllegalArgumentException("totalMl must be >= 0");
        if (goalMl == null || goalMl <= 0)
            throw new IllegalArgumentException("goalMl must be > 0");
        if (recordedAt == null)
            throw new IllegalArgumentException("recordedAt must not be null");
    }

    public float progressPercentage() {
        return Math.min((totalMl / goalMl) * 100.0f, 100.0f);
    }

    public boolean isGoalReached() {
        return totalMl >= goalMl;
    }
}

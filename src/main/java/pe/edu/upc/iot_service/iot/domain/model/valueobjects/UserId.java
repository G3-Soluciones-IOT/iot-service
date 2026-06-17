package pe.edu.upc.iot_service.iot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long value) {
    public UserId {
        if (value == null || value <= 0)
            throw new IllegalArgumentException("UserId must be a positive number");
    }
}

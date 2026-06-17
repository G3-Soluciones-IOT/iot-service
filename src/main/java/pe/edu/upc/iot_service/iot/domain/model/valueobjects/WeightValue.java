package pe.edu.upc.iot_service.iot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record WeightValue(Float value) {
    public WeightValue {
        if (value == null || value < 0.1f || value > 5000.0f)
            throw new IllegalArgumentException("WeightValue must be between 0.1 and 5000 grams");
    }
}

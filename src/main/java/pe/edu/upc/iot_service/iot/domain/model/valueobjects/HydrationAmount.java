package pe.edu.upc.iot_service.iot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record HydrationAmount(Float value) {
    public HydrationAmount {
        if (value == null || value < 1.0f || value > 2000.0f)
            throw new IllegalArgumentException("HydrationAmount must be between 1 and 2000 ml");
    }
}

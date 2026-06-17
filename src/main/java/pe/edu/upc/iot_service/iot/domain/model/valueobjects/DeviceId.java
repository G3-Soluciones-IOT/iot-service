package pe.edu.upc.iot_service.iot.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import org.apache.commons.lang3.StringUtils;

@Embeddable
public record DeviceId(String value) {
    public DeviceId {
        if (StringUtils.isBlank(value) || value.length() > 64)
            throw new IllegalArgumentException("DeviceId must be non-blank and at most 64 characters");
    }
}

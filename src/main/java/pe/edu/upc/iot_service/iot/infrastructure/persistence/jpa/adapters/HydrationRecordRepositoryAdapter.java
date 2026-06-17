package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.adapters;

import org.springframework.stereotype.Component;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.services.HydrationRecordRepository;
import pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories.JpaHydrationRecordRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Adapter between the domain repository interface and the JPA repository.
 * This keeps JPA details out of the domain layer.
 */
@Component
public class HydrationRecordRepositoryAdapter implements HydrationRecordRepository {

    private final JpaHydrationRecordRepository jpa;

    public HydrationRecordRepositoryAdapter(JpaHydrationRecordRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public HydrationRecord save(HydrationRecord record) {
        return jpa.save(record);
    }

    @Override
    public Optional<HydrationRecord> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<HydrationRecord> findByUserIdAndRecordedAtBetween(Long userId, Instant from, Instant to) {
        return jpa.findByUserIdAndRecordedAtBetween(userId, from, to);
    }

    @Override
    public Float getMaxTotalMlByUserIdAndDate(Long userId, LocalDate date) {
        return jpa.getMaxTotalMlByUserIdAndDate(userId, date);
    }
}

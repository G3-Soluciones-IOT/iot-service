package pe.edu.upc.iot_service.iot.domain.model.services;

import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface HydrationRecordRepository {
    HydrationRecord save(HydrationRecord record);
    Optional<HydrationRecord> findById(Long id);
    List<HydrationRecord> findByUserIdAndRecordedAtBetween(Long userId, Instant from, Instant to);
    Float getMaxTotalMlByUserIdAndDate(Long userId, java.time.LocalDate date);
}

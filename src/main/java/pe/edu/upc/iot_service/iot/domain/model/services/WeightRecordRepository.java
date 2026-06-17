package pe.edu.upc.iot_service.iot.domain.model.services;

import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WeightRecordRepository {
    WeightRecord save(WeightRecord record);
    Optional<WeightRecord> findById(Long id);
    List<WeightRecord> findByUserIdAndRecordedAtBetween(Long userId, Instant from, Instant to);
    Optional<WeightRecord> findLatestByUserId(Long userId);
}

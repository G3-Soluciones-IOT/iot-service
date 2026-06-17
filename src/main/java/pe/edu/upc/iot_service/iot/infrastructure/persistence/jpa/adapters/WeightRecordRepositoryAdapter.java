package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.adapters;

import org.springframework.stereotype.Component;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.services.WeightRecordRepository;
import pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories.JpaWeightRecordRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class WeightRecordRepositoryAdapter implements WeightRecordRepository {

    private final JpaWeightRecordRepository jpa;

    public WeightRecordRepositoryAdapter(JpaWeightRecordRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public WeightRecord save(WeightRecord record) {
        return jpa.save(record);
    }

    @Override
    public Optional<WeightRecord> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    public List<WeightRecord> findByUserIdAndRecordedAtBetween(Long userId, Instant from, Instant to) {
        return jpa.findByUserIdAndRecordedAtBetween(userId, from, to);
    }

    @Override
    public Optional<WeightRecord> findLatestByUserId(Long userId) {
        return jpa.findLatestByUserId(userId);
    }
}

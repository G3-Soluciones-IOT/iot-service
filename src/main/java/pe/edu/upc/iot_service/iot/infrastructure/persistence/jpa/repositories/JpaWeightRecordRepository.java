package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaWeightRecordRepository extends JpaRepository<WeightRecord, Long> {

    @Query("SELECT w FROM WeightRecord w " +
           "WHERE w.userId.value = :userId " +
           "AND w.recordedAt >= :from AND w.recordedAt < :to " +
           "ORDER BY w.recordedAt ASC")
    List<WeightRecord> findByUserIdAndRecordedAtBetween(
            @Param("userId") Long userId,
            @Param("from")   Instant from,
            @Param("to")     Instant to);

    @Query("SELECT w FROM WeightRecord w " +
           "WHERE w.userId.value = :userId " +
           "ORDER BY w.recordedAt DESC " +
           "LIMIT 1")
    Optional<WeightRecord> findLatestByUserId(@Param("userId") Long userId);
}

package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaHydrationRecordRepository extends JpaRepository<HydrationRecord, Long> {

    @Query("SELECT h FROM HydrationRecord h " +
           "WHERE h.userId.value = :userId " +
           "AND h.recordedAt >= :from AND h.recordedAt < :to " +
           "ORDER BY h.recordedAt ASC")
    List<HydrationRecord> findByUserIdAndRecordedAtBetween(
            @Param("userId") Long userId,
            @Param("from")   Instant from,
            @Param("to")     Instant to);

    @Query("SELECT COALESCE(MAX(h.totalMl), 0.0) FROM HydrationRecord h " +
           "WHERE h.userId.value = :userId " +
           "AND CAST(h.recordedAt AS date) = :date")
    Float getMaxTotalMlByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date")   LocalDate date);
}

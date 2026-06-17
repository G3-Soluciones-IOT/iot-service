package pe.edu.upc.iot_service.iot.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaIotDeviceRepository extends JpaRepository<IotDevice, Long> {

    Optional<IotDevice> findByDeviceId(String deviceId);

    @Query("SELECT d FROM IotDevice d WHERE d.userId.value = :userId")
    List<IotDevice> findByUserId(@Param("userId") Long userId);

    boolean existsByDeviceId(String deviceId);
}

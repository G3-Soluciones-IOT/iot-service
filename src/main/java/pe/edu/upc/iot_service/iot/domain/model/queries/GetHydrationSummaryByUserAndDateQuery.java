package pe.edu.upc.iot_service.iot.domain.model.queries;
import java.time.LocalDate;
public record GetHydrationSummaryByUserAndDateQuery(Long userId, LocalDate date) {}

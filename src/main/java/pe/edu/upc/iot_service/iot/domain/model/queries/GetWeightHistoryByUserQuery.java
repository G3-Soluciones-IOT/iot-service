package pe.edu.upc.iot_service.iot.domain.model.queries;
import java.time.LocalDate;
public record GetWeightHistoryByUserQuery(Long userId, LocalDate from, LocalDate to) {}

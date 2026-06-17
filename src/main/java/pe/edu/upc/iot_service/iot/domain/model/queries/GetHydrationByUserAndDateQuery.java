package pe.edu.upc.iot_service.iot.domain.model.queries;

import java.time.LocalDate;

public record GetHydrationByUserAndDateQuery(Long userId, LocalDate date) {}

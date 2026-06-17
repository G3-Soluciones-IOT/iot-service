package pe.edu.upc.iot_service.iot.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationByUserAndDateQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationSummaryByUserAndDateQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.HydrationQueryService;
import pe.edu.upc.iot_service.iot.domain.model.services.HydrationRecordRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class HydrationQueryServiceImpl implements HydrationQueryService {

    private final HydrationRecordRepository repository;

    public HydrationQueryServiceImpl(HydrationRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<HydrationRecord> handle(GetHydrationByUserAndDateQuery query) {
        var from = query.date().atStartOfDay().toInstant(ZoneOffset.UTC);
        var to   = query.date().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return repository.findByUserIdAndRecordedAtBetween(query.userId(), from, to);
    }

    @Override
    public Map<String, Object> handle(GetHydrationSummaryByUserAndDateQuery query) {
        LocalDate date    = query.date();
        Float     total   = repository.getMaxTotalMlByUserIdAndDate(query.userId(), date);
        if (total == null) total = 0.0f;

        // Goal is not stored in this service; use last known goalMl from most recent record
        var from    = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        var to      = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        var records = repository.findByUserIdAndRecordedAtBetween(query.userId(), from, to);
        Float goal  = records.isEmpty() ? 2500.0f : records.getLast().getGoalMl();

        float pct     = Math.min((total / goal) * 100.0f, 100.0f);
        boolean reach = total >= goal;

        return Map.of(
                "userId",             query.userId(),
                "date",               date.toString(),
                "totalMl",            total,
                "goalMl",             goal,
                "progressPercentage", pct,
                "goalReached",        reach
        );
    }
}

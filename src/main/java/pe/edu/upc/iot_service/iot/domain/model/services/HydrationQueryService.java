package pe.edu.upc.iot_service.iot.domain.model.services;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationByUserAndDateQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationSummaryByUserAndDateQuery;
import java.util.List;
import java.util.Map;
public interface HydrationQueryService {
    List<HydrationRecord> handle(GetHydrationByUserAndDateQuery query);
    Map<String, Object> handle(GetHydrationSummaryByUserAndDateQuery query);
}

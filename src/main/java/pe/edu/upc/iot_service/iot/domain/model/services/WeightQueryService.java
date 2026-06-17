package pe.edu.upc.iot_service.iot.domain.model.services;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetLatestWeightByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetWeightHistoryByUserQuery;
import java.util.List;
import java.util.Optional;
public interface WeightQueryService {
    List<WeightRecord> handle(GetWeightHistoryByUserQuery query);
    Optional<WeightRecord> handle(GetLatestWeightByUserQuery query);
}

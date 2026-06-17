package pe.edu.upc.iot_service.iot.application.internal.queryservices;

import org.springframework.stereotype.Service;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetLatestWeightByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetWeightHistoryByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.WeightQueryService;
import pe.edu.upc.iot_service.iot.domain.model.services.WeightRecordRepository;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class WeightQueryServiceImpl implements WeightQueryService {

    private final WeightRecordRepository repository;

    public WeightQueryServiceImpl(WeightRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WeightRecord> handle(GetWeightHistoryByUserQuery query) {
        var from = query.from().atStartOfDay().toInstant(ZoneOffset.UTC);
        var to   = query.to().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        return repository.findByUserIdAndRecordedAtBetween(query.userId(), from, to);
    }

    @Override
    public Optional<WeightRecord> handle(GetLatestWeightByUserQuery query) {
        return repository.findLatestByUserId(query.userId());
    }
}

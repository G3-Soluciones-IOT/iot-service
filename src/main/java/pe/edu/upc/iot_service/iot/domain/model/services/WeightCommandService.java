package pe.edu.upc.iot_service.iot.domain.model.services;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordWeightCommand;
public interface WeightCommandService {
    WeightRecord handle(RecordWeightCommand command);
}

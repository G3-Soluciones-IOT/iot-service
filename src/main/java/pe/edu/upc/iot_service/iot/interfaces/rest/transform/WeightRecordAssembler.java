package pe.edu.upc.iot_service.iot.interfaces.rest.transform;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.WeightRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordWeightCommand;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.RecordWeightResource;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.WeightRecordResponse;
public class WeightRecordAssembler {
    public static RecordWeightCommand toCommand(RecordWeightResource r) {
        return new RecordWeightCommand(r.userId(), r.deviceId(), r.grams(), r.measurementType(), r.ts());
    }
    public static WeightRecordResponse toResponse(WeightRecord e) {
        return new WeightRecordResponse(e.getId(), e.getUserId().value(),
                e.getDeviceId().value(), e.getGrams().value(),
                e.getMeasurementType(), e.getRecordedAt());
    }
}

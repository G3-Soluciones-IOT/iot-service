package pe.edu.upc.iot_service.iot.interfaces.rest.transform;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordHydrationCommand;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.HydrationRecordResponse;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.RecordHydrationResource;
public class HydrationRecordAssembler {
    public static RecordHydrationCommand toCommand(RecordHydrationResource r) {
        return new RecordHydrationCommand(r.userId(), r.deviceId(), r.ml(), r.totalMl(), r.goalMl(), r.ts());
    }
    public static HydrationRecordResponse toResponse(HydrationRecord e) {
        return new HydrationRecordResponse(e.getId(), e.getUserId().value(),
                e.getDeviceId().value(), e.getAmountMl().value(),
                e.getTotalMl(), e.getGoalMl(),
                e.progressPercentage(), e.isGoalReached(), e.getRecordedAt());
    }
}

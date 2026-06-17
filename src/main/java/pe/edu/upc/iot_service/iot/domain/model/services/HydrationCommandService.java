package pe.edu.upc.iot_service.iot.domain.model.services;

import pe.edu.upc.iot_service.iot.domain.model.aggregates.HydrationRecord;
import pe.edu.upc.iot_service.iot.domain.model.commands.RecordHydrationCommand;

public interface HydrationCommandService {
    HydrationRecord handle(RecordHydrationCommand command);
}

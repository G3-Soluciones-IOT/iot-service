package pe.edu.upc.iot_service.iot.domain.model.services;
import pe.edu.upc.iot_service.iot.domain.model.aggregates.IotDevice;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetDevicesByUserQuery;
import java.util.List;
public interface DeviceQueryService {
    List<IotDevice> handle(GetDevicesByUserQuery query);
}

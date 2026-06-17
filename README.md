# iot-service

Microservicio del **Bounded Context IoT Device Data** para la plataforma **JameoFit**.  
Recibe, valida y persiste los datos capturados por los dispositivos embebidos **Smart Bottle** y **Smart Scale** (ESP32 DevKit V1), y expone endpoints de consulta para la aplicación web y mobile.

---

## Descripción general

| Aspecto | Detalle |
|---|---|
| Puerto | `8091` |
| Framework | Spring Boot 3.x – Java 25 |
| Base de datos | PostgreSQL (instancia propia: `iot_db`) |
| Autenticación dispositivos | `X-API-Key` header (BCrypt) |
| Autenticación web/mobile | JWT Bearer validado contra `iam-service` JWKS |
| Protocolo IoT | HTTP REST (principal) · MQTT previsto en arquitectura |
| Service discovery | Eureka |
| Config center | Spring Cloud Config (`iot-service.yml`) |

---

## Estructura del proyecto

```
iot-service/
├── src/main/java/pe/edu/upc/iot_service/
│   ├── IotServiceApplication.java
│   │
│   ├── iot/
│   │   ├── domain/model/
│   │   │   ├── aggregates/          HydrationRecord, WeightRecord, IotDevice
│   │   │   ├── valueobjects/        UserId, DeviceId, HydrationAmount, WeightValue,
│   │   │   │                        DeviceType, MeasurementType, DeviceStatus
│   │   │   ├── commands/            RecordHydrationCommand, RecordWeightCommand,
│   │   │   │                        RegisterDeviceCommand, UnregisterDeviceCommand
│   │   │   ├── queries/             GetHydrationByUserAndDateQuery,
│   │   │   │                        GetHydrationSummaryByUserAndDateQuery,
│   │   │   │                        GetWeightHistoryByUserQuery,
│   │   │   │                        GetLatestWeightByUserQuery,
│   │   │   │                        GetDevicesByUserQuery
│   │   │   ├── exceptions/          DeviceNotFoundException,
│   │   │   │                        DeviceAlreadyRegisteredException,
│   │   │   │                        UnauthorizedDeviceException,
│   │   │   │                        DeviceInactiveException
│   │   │   └── services/            HydrationRecordRepository, WeightRecordRepository,
│   │   │                            IotDeviceRepository, ApiKeyService,
│   │   │                            HydrationCommandService, WeightCommandService,
│   │   │                            DeviceCommandService, HydrationQueryService,
│   │   │                            WeightQueryService, DeviceQueryService
│   │   │
│   │   ├── application/internal/
│   │   │   ├── commandservices/     HydrationCommandServiceImpl,
│   │   │   │                        WeightCommandServiceImpl,
│   │   │   │                        DeviceCommandServiceImpl,
│   │   │   │                        IotDeviceWithRawKey (thread-local helper)
│   │   │   └── queryservices/       HydrationQueryServiceImpl,
│   │   │                            WeightQueryServiceImpl,
│   │   │                            DeviceQueryServiceImpl
│   │   │
│   │   ├── infrastructure/
│   │   │   ├── persistence/jpa/
│   │   │   │   ├── repositories/    JpaHydrationRecordRepository,
│   │   │   │   │                    JpaWeightRecordRepository,
│   │   │   │   │                    JpaIotDeviceRepository
│   │   │   │   └── adapters/        HydrationRecordRepositoryAdapter,
│   │   │   │                        WeightRecordRepositoryAdapter,
│   │   │   │                        IotDeviceRepositoryAdapter
│   │   │   └── security/            ApiKeyServiceImpl,
│   │   │                            ApiKeyAuthenticationFilter,
│   │   │                            WebSecurityConfiguration
│   │   │
│   │   └── interfaces/rest/
│   │       ├── HydrationController
│   │       ├── WeightController
│   │       ├── DeviceController
│   │       ├── resources/           RecordHydrationResource, RecordWeightResource,
│   │       │                        RegisterDeviceResource, HydrationRecordResponse,
│   │       │                        WeightRecordResponse, DeviceRegisteredResponse,
│   │       │                        DeviceResponse
│   │       └── transform/           HydrationRecordAssembler, WeightRecordAssembler,
│   │                                DeviceAssembler
│   │
│   └── shared/
│       ├── domain/model/aggregates/ AuditableAbstractAggregateRoot
│       ├── infrastructure/
│       │   ├── persistence/jpa/configuration/strategy/
│       │   │                        SnakeCaseWithPluralizedTablePhysicalNamingStrategy
│       │   └── documentation/openapi/configuration/
│       │                            OpenApiConfiguration
│       └── interfaces/rest/
│           ├── exception/           GlobalExceptionHandler
│           └── resources/           ErrorResponseResource
│
└── src/main/resources/
    └── application.properties
```

---

## Tablas en base de datos

| Tabla | Descripción |
|---|---|
| `hydration_records` | Cada sorbo detectado por la Smart Bottle |
| `weight_records` | Cada medición estabilizada de la Smart Scale |
| `iot_devices` | Dispositivos registrados con su hash de API Key |

> El naming strategy pluraliza y convierte a snake_case automáticamente. No es necesario definir `@Table(name=...)` en los aggregates.

---

## Endpoints

### Dispositivo → Backend (`X-API-Key`)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/iot/hydration` | Registra un sorbo de agua |
| `POST` | `/api/v1/iot/weight` | Registra una medición de peso |

### Web / Mobile → Backend (JWT Bearer)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/v1/iot/devices` | Registra un dispositivo (retorna API Key una sola vez) |
| `GET` | `/api/v1/iot/devices/{userId}` | Lista dispositivos del usuario |
| `DELETE` | `/api/v1/iot/devices/{userId}/{deviceId}` | Desvincula un dispositivo |
| `GET` | `/api/v1/iot/hydration/{userId}?date=` | Registros de hidratación por fecha |
| `GET` | `/api/v1/iot/hydration/{userId}/summary?date=` | Resumen diario de hidratación |
| `GET` | `/api/v1/iot/weight/{userId}/history?from=&to=` | Historial de peso en rango de fechas |
| `GET` | `/api/v1/iot/weight/{userId}/latest` | Última medición de peso |

---

## Cómo ejecutar localmente

```bash
# 1. Levantar PostgreSQL con la base de datos correcta
docker run -e POSTGRES_DB=iot_db -e POSTGRES_PASSWORD=password -p 5433:5432 postgres:15

# 2. Ejecutar el microservicio
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-DPOSTGRES_HOST=localhost -DPOSTGRES_PORT=5433 -DPOSTGRES_DATABASE=iot_db"
```

La documentación Swagger estará disponible en:  
`http://localhost:8091/swagger-ui/index.html`

---

## Variables de entorno requeridas en producción

| Variable | Descripción |
|---|---|
| `POSTGRES_HOST` | Host de la base de datos PostgreSQL |
| `POSTGRES_PORT` | Puerto PostgreSQL (default: 5432) |
| `POSTGRES_DATABASE` | Nombre de la base de datos (`iot_db`) |
| `POSTGRES_USER` | Usuario PostgreSQL |
| `POSTGRES_PASSWORD` | Contraseña PostgreSQL |
| `JWT_JWK_SET_URI` | URL del JWKS del iam-service |
| `IOT_API_KEY_SALT` | Strength de BCrypt para las API Keys (default: 12) |

---

## Integración con el ecosistema JameoFit

```
Smart Bottle (ESP32)  ──POST /api/v1/iot/hydration──►  iot-service  ──►  PostgreSQL (iot_db)
Smart Scale  (ESP32)  ──POST /api/v1/iot/weight ──────►  iot-service  ──►  PostgreSQL (iot_db)

Web App / Mobile  ─────GET  /api/v1/iot/...  ──────────►  iot-service
                  ─────(via gateway-service en puerto 8080)

iot-service  ──────────valida JWT contra──────────────►  iam-service (JWKS :8081)
iot-service  ──────────se registra en──────────────────►  eureka-service (:8761)
iot-service  ──────────obtiene config de───────────────►  config-service (:8888)
```

---

*JF Technologies · NRC 17756 · 1ASI0572 Desarrollo de Soluciones IoT · 2026-10*

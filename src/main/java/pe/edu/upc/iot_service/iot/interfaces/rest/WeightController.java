package pe.edu.upc.iot_service.iot.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetLatestWeightByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetWeightHistoryByUserQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.WeightCommandService;
import pe.edu.upc.iot_service.iot.domain.model.services.WeightQueryService;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.RecordWeightResource;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.WeightRecordResponse;
import pe.edu.upc.iot_service.iot.interfaces.rest.transform.WeightRecordAssembler;

import java.time.LocalDate;
import java.util.List;

/**
 * Exposes two groups of endpoints:
 *   - POST /api/v1/iot/weight     → called by the Smart Scale firmware (X-API-Key auth)
 *   - GET  /api/v1/iot/weight     → called by Web/Mobile (JWT auth)
 */
@RestController
@RequestMapping(value = "/api/v1/iot/weight", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Weight", description = "Smart Scale – weight ingestion and query endpoints")
public class WeightController {

    private final WeightCommandService commandService;
    private final WeightQueryService   queryService;

    public WeightController(WeightCommandService commandService,
                             WeightQueryService queryService) {
        this.commandService = commandService;
        this.queryService   = queryService;
    }

    // ── Device endpoint ────────────────────────────────────────────────────────

    @Operation(summary = "Record a weight measurement (device endpoint)",
               description = "Called by the Smart Scale firmware when a stable weight is detected. Requires X-API-Key header.",
               security = @SecurityRequirement(name = "apiKeyAuth"))
    @PostMapping
    public ResponseEntity<WeightRecordResponse> record(
            @Valid @RequestBody RecordWeightResource resource) {
        var command  = WeightRecordAssembler.toCommand(resource);
        var saved    = commandService.handle(command);
        var response = WeightRecordAssembler.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Web / Mobile query endpoints ───────────────────────────────────────────

    @Operation(summary = "Get weight history by user and date range",
               description = "Returns all weight records between from and to dates (inclusive). Requires JWT.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<WeightRecordResponse>> getHistory(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var query   = new GetWeightHistoryByUserQuery(userId, from, to);
        var records = queryService.handle(query);
        var response = records.stream()
                .map(WeightRecordAssembler::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get latest weight measurement",
               description = "Returns the most recent weight record for the user. Requires JWT.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}/latest")
    public ResponseEntity<WeightRecordResponse> getLatest(@PathVariable Long userId) {
        var query  = new GetLatestWeightByUserQuery(userId);
        var record = queryService.handle(query);
        return record.map(r -> ResponseEntity.ok(WeightRecordAssembler.toResponse(r)))
                     .orElse(ResponseEntity.notFound().build());
    }
}

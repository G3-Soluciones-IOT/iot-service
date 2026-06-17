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
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationByUserAndDateQuery;
import pe.edu.upc.iot_service.iot.domain.model.queries.GetHydrationSummaryByUserAndDateQuery;
import pe.edu.upc.iot_service.iot.domain.model.services.HydrationCommandService;
import pe.edu.upc.iot_service.iot.domain.model.services.HydrationQueryService;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.HydrationRecordResponse;
import pe.edu.upc.iot_service.iot.interfaces.rest.resources.RecordHydrationResource;
import pe.edu.upc.iot_service.iot.interfaces.rest.transform.HydrationRecordAssembler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Exposes two groups of endpoints:
 *   - POST /api/v1/iot/hydration  → called by the Smart Bottle firmware (X-API-Key auth)
 *   - GET  /api/v1/iot/hydration  → called by Web/Mobile (JWT auth)
 */
@RestController
@RequestMapping(value = "/api/v1/iot/hydration", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Hydration", description = "Smart Bottle – hydration ingestion and query endpoints")
public class HydrationController {

    private final HydrationCommandService commandService;
    private final HydrationQueryService   queryService;

    public HydrationController(HydrationCommandService commandService,
                                HydrationQueryService queryService) {
        this.commandService = commandService;
        this.queryService   = queryService;
    }

    // ── Device endpoint ────────────────────────────────────────────────────────

    @Operation(summary = "Record a hydration event (device endpoint)",
               description = "Called by the Smart Bottle firmware. Requires X-API-Key header.",
               security = @SecurityRequirement(name = "apiKeyAuth"))
    @PostMapping
    public ResponseEntity<HydrationRecordResponse> record(
            @Valid @RequestBody RecordHydrationResource resource) {
        var command  = HydrationRecordAssembler.toCommand(resource);
        var saved    = commandService.handle(command);
        var response = HydrationRecordAssembler.toResponse(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Web / Mobile query endpoints ───────────────────────────────────────────

    @Operation(summary = "Get hydration records by user and date",
               description = "Returns all sip events for a user on a given date. Requires JWT.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}")
    public ResponseEntity<List<HydrationRecordResponse>> getByUserAndDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var query   = new GetHydrationByUserAndDateQuery(userId, date);
        var records = queryService.handle(query);
        var response = records.stream()
                .map(HydrationRecordAssembler::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get hydration daily summary",
               description = "Returns totalMl, goalMl, progressPercentage and goalReached for a given date.",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        var query   = new GetHydrationSummaryByUserAndDateQuery(userId, date);
        var summary = queryService.handle(query);
        return ResponseEntity.ok(summary);
    }
}

package idhub.sortinparcels.controller;

import idhub.sortinparcels.dto.AuditEvent;
import idhub.sortinparcels.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Audit", description = "Log and history of scanned parcels")
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @Operation(summary = "Get all audit events",
            description = "Returns complete history of parcel scan operations")
    @GetMapping("/all")
    public List<AuditEvent> getAllEvents() {

        return auditService.getAllEvents()
                .stream()
                .map(AuditEvent::fromEntity)
                .toList();
    }

    @Operation(summary = "Get audit events by session ID",
            description = "Returns all scan events grouped by a specific scanning session")
    @GetMapping("/session/{sessionId}")
    public List<AuditEvent> getEventsBySession(
            @Parameter(description = "Session identifier", example = "SHIFT-2025-03-20-A")
            @PathVariable String sessionId) {

        return auditService.getEventsBySession(sessionId)
                .stream()
                .map(AuditEvent::fromEntity)
                .toList();
    }


    @Operation(summary = "Get audit events by tracking number",
            description = "Returns a full history of operations performed on a specific parcel")
    @GetMapping("/parcel/{trackingNumber}")
    public List<AuditEvent> getEventsByTrackingNumber(
            @Parameter(description = "Parcel tracking number", example = "JD0146000065427282")
            @PathVariable String trackingNumber) {

        return auditService.getEventsByTrackingNumber(trackingNumber)
                .stream()
                .map(AuditEvent::fromEntity)
                .toList();
    }
}

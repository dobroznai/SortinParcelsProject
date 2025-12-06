package idhub.sortinparcels.dto;

import idhub.sortinparcels.enums.AuditEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an audit event for a parcel")
public class AuditEvent {

    @Schema(description = "Message about the result of the operation", example = "Parcel scanned successfully")
    private String message;

    @Schema(description = "Tracking number of the parcel", example = "JD0146000065427282")
    private String trackingNumber;

    @Schema(description = "Type of audit event", example = "SCANNED")
    private AuditEventType event;

    @Schema(description = "User/device who performed the scan", example = "scanner123")
    private String scannedBy;

    @Schema(description = "Timestamp when the event occurred")
    private Instant scannedAt;

    @Schema(description = "Session identifier for grouping scanned parcels")
    private String sessionId;


    public static AuditEvent successScan(String trackingNumber, String scannedBy, String sessionId) {
        return new AuditEvent(
                null,
                trackingNumber,
                AuditEventType.SCANNED,
                scannedBy,
                Instant.now(),
                sessionId
        );
    }

    public static AuditEvent repeatedScan(String trackingNumber, String scannedBy, String sessionId) {
        return new AuditEvent(
                "Parcel was already scanned",
                trackingNumber,
                AuditEventType.REPEATED_SCAN,
                scannedBy,
                Instant.now(),
                sessionId
        );
    }

    public static AuditEvent invalidScan(String trackingNumber, String scannedBy, String sessionId, String reason) {
        return new AuditEvent(
                "Invalid scan: " + reason,
                trackingNumber,
                AuditEventType.INVALID_SCAN,
                scannedBy,
                Instant.now(),
                sessionId
        );
    }
}
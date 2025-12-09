package idhub.sortinparcels.dto;

import idhub.sortinparcels.enums.AuditEventType;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.model.ParcelAudit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO representing an audit record generated during parcel processing.
 * <p>
 * Used to capture and transfer information about important events,
 * such as successful scans, repeated scans, or invalid operations.
 * <br><br>
 * This object is NOT stored directly in the database.
 * Instead, it can be converted into {@link ParcelAudit} using {@link #toEntity()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an audit event for a parcel")
public class AuditEvent {

    @Schema(description = "Optional message describing the event result", example = "Parcel was already scanned")
    private String message;

    @Schema(description = "Tracking number of the parcel involved in the event", example = "JD0146000065427282")
    private String trackingNumber;

    @Schema(description = "Type of audit event", example = "SCANNED")
    private AuditEventType event;

    @Schema(description = "User or device that triggered the event", example = "scanner01")
    private String scannedBy;

    @Schema(description = "Timestamp when the event occurred (UTC format)")
    private Instant scannedAt;

    @Schema(description = "Identifier for grouping events into a scanning session", example = "SHIFT-2025-03-05-A")
    private String sessionId;

    /**
     * Creates an audit object for a successful parcel scan event.
     *
     * @param parcel    The scanned parcel.
     * @param scannedBy ID of the employee/device who scanned the parcel.
     * @param sessionId Identifier of the scanning session.
     * @return DTO containing audit information.
     */
    public static AuditEvent successScan(Parcel parcel, String scannedBy, String sessionId) {
        return new AuditEvent(
                "Parcel scanned successfully - " + parcel.getTrackingNumber(),
                parcel.getTrackingNumber(),
                AuditEventType.SCANNED,
                scannedBy,
                Instant.now(),
                sessionId);
    }

    /**
     * Creates an audit object for a repeated scanning attempt.
     *
     * @param parcel    The parcel that was previously scanned.
     * @param scannedBy ID of the employee/device who attempted the scan.
     * @param sessionId Identifier of the scanning session.
     * @return DTO containing audit information and a warning message.
     */
    public static AuditEvent repeatedScan(Parcel parcel, String scannedBy, String sessionId) {
        return new AuditEvent(
                "The parcel has already been scanned - " + parcel.getTrackingNumber(),
                parcel.getTrackingNumber(),
                AuditEventType.REPEATED_SCAN,
                scannedBy,
                Instant.now(),
                sessionId);
    }

    /**
     * Creates an audit object for an invalid scan attempt (wrong status, wrong zone, etc.).
     *
     * @param parcel    The affected parcel.
     * @param scannedBy ID of the employee/device who attempted the scan.
     * @param sessionId Identifier of the scanning session.
     * @param reason    Explanation why scanning failed.
     * @return DTO containing audit information and an error message.
     */
    public static AuditEvent invalidScan(Parcel parcel, String scannedBy, String sessionId, String reason) {
        return new AuditEvent(
                "Parcel " + parcel.getTrackingNumber() + " invalid scan: " + reason,
                parcel.getTrackingNumber(),
                AuditEventType.INVALID_SCAN,
                scannedBy,
                Instant.now(),
                sessionId);
    }

    /**
     * Converts this DTO into a persistent entity for database storage.
     * <p>
     * Used by the service layer before inserting into {@link idhub.sortinparcels.repository.ParcelAuditRepository}.
     *
     * @return A {@link ParcelAudit} entity ready for persistence.
     */
    public ParcelAudit toEntity() {
        return new ParcelAudit(
                this.trackingNumber,
                this.event,
                this.scannedBy,
                this.scannedAt,
                this.sessionId,
                this.message);
    }

    public static AuditEvent fromEntity(ParcelAudit audit) {
        return new AuditEvent(
                audit.getMessage(),          // message may be null
                audit.getTrackingNumber(),
                audit.getEvent(),
                audit.getScannedBy(),
                audit.getScannedAt(),
                audit.getSessionId()
        );
    }
}
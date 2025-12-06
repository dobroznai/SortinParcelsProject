package idhub.sortinparcels.model;

import idhub.sortinparcels.enums.AuditEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Table(name = "parcel_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 *Keeps a history of all changes/events
 */
public class ParcelAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEventType event;

    @Column(nullable = false)
    private String scannedBy;

    @Column(nullable = false)
    private Instant scannedAt;

    @Column(nullable = false)
    private String sessionId;

    public ParcelAudit(String trackingNumber, AuditEventType event, String scannedBy, Instant scannedAt, String sessionId) {
        this.trackingNumber = trackingNumber;
        this.event = event;
        this.scannedBy = scannedBy;
        this.scannedAt = scannedAt;
        this.sessionId = sessionId;
    }
}
package idhub.sortinparcels.repository;

import idhub.sortinparcels.model.ParcelAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParcelAuditRepository extends JpaRepository<ParcelAudit, Long> {
    /**
     * Latest audit event for a parcel (most recent scan)
     */
    Optional<ParcelAudit> findFirstByTrackingNumberOrderByScannedAtDesc(String trackingNumber);

    /**
     * Latest audit event for a session
     */
    Optional<ParcelAudit> findFirstBySessionIdOrderByScannedAtDesc(String sessionId);

    /**
     * All audit events for a specific parcel
     */
    List<ParcelAudit> findByTrackingNumber(String trackingNumber);

    /**
     * All audit events for a specific scanning session
     */
    List<ParcelAudit> findBySessionId(String sessionId);


    /**
     * Filter events by type for a specific parcel
     */
    List<ParcelAudit> findByTrackingNumberAndEventOrderByScannedAtDesc(String trackingNumber, idhub.sortinparcels.enums.AuditEventType event);
}
package idhub.sortinparcels.model;

import idhub.sortinparcels.enums.ParcelStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Table(name = "parcels",
        // Faster data search in the DB.
        // Index (idx) on the "parcel" table created for quick search by tracking_number columns
        indexes = {@Index(name = "idx_parcel_tracking_number", columnList = "tracking_number", unique = true),
                @Index(name = "idx_parcel_zone_route", columnList = "zone_code route_number")
        }
)

public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique tracking code provided by a logistic partner.
     * <p>
     * Used by scanners to identify a shipment.
     * <p><b>Example:</b> {@code JD0146000065427282}
     */
    @Column(nullable = false, unique = true, name = "tracking_number", length = 40)
    private String trackingNumber;

    /**
     * Warehouse sorting zone (cluster of streets handled by a group).
     * <p><b>Example:</b> {@code 80-08}
     * <p>
     * Defined by internal logistics standard.
     */
    @Column(nullable = false, name = "zone_code", length = 5)
    private String zoneCode;

    /**
     * Courier delivery route identifier.
     * <p><b>Example:</b> {@code 010}
     * Represents the driver's delivery tour.
     */
    @Column(nullable = false, name = "route_number", length = 3)
    private String routeNumber;

    /**
     * Current parcel state in the sorting process (Pending, Scanned, Delivered).
     *
     * @see ParcelStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    /**
     * Timestamp when parcel was scanned for sorting or delivery.
     */
    @Column(nullable = false, name = "scanned_at")
    private Instant scannedAt;

    /**
     * User identifier (username or device ID) who scanned the parcel.
     */
    @Column(nullable = false, name = "scanned_by", length = 50)
    private String scannedBy;

    /**
     * Creation timestamp generated automatically during first persistence.
     * <p>
     * "Updatable" So that it doesn't get updated accidentally.
     */
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;


    /**
     * Last update timestamp applied before update persistence.
     */
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    /**
     * Optimistic locking version — protects against concurrent scanning attempts.
     * <p>
     * Prevents a parcel from being scanned twice at the same time by two workers.
     */
    @Version
    private Long version;

    /**
     * Predefined constructor to enforce mandatory business fields.
     *
     * @param trackingNumber Unique tracking identifier.
     * @param zoneCode       Warehouse sorting zone code.
     * @param routeNumber    Courier delivery route.
     * @param status         Parcel status.
     */
    public Parcel(String trackingNumber, String zoneCode, String routeNumber, ParcelStatus status) {
        this.trackingNumber = trackingNumber;
        this.zoneCode = zoneCode;
        this.routeNumber = routeNumber;
        this.status = status;
    }

    /**
     * Auto-assigns creation and update timestamps on first persistence.
     * Triggered automatically by JPA.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Updates timestamp on every database update call.
     * Ensures data consistency for reporting and sorting analytics.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
package idhub.sortinparcels.repository;

import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Provides CRUD operations and domain queries for working with packages
 * The naming method (derived queries) allows you to perform searches without writing SQL
 */
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    /**
     * Find parcel by unique tracking number used during scanning.
     * SELECT * FROM parcels WHERE trackingNumber = ? LIMIT 1
     */
    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    /**
     * Get parcels filtered by sorting status (PENDING, SCANNED, DELIVERED).
     */
    List<Parcel> findByStatus(ParcelStatus status);

    /**
     * Get all parcels assigned to a specific courier route (tour).
     */
    List<Parcel> findByRouteNumber(String routeNumber);


    /**
     * Find parcels filtered by zone + route.
     * Uses database index to speed up scanning operations.
     * SELECT * FROM parcels WHERE zone_code = ? AND route_number = ?
     */
    List<Parcel> findByZoneCodeAndRouteNumber(String zoneCode, String routeNumber);

    /**
     * Check if parcel exists by tracking number without executing SELECT payload.
     */
    boolean existsByTrackingNumber(String trackingNumber);

    /**
     * Mass delete by status — returns deleted rows count.
     * Safer than void-delete.
     */
    long deleteByStatus(ParcelStatus status);

}

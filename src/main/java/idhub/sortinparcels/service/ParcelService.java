package idhub.sortinparcels.service;

import idhub.sortinparcels.dto.AuditEvent;
import idhub.sortinparcels.dto.ParcelReaderDto;
import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.exceptions.ParcelNotFoundException;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.model.ParcelAudit;
import idhub.sortinparcels.repository.ParcelAuditRepository;
import idhub.sortinparcels.repository.ParcelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service layer responsible for processing parcel scanning operations
 * and maintaining related audit history.
 *
 * <p>The primary tasks of this service include:
 * <ul>
 *     <li>Validating parcel existence by tracking number.</li>
 *     <li>Changing parcel status to {@code SCANNED} if it has not been scanned before.</li>
 *     <li>Handling repeated scan attempts and preventing data override.</li>
 *     <li>Persisting audit logs for each scanning action.</li>
 *     <li>Providing access to historical scan data grouped by scanning session.</li>
 * </ul>
 *
 * <p>This logic is used by warehouse workers or automated scanners
 * to track and verify sorting operations during parcel handling.
 */
@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelAuditRepository auditRepository;

    @Transactional
    public int importParcelsFromDto(List<ParcelReaderDto> dtoList) {

        // 1. Отримуємо всі існуючі трекінг-номери в Set для O(1) перевірок
        // .contains() на List - existing.contains(dto.getTrackingNumber()) — це O(N) на кожну перевірку.
        Set<String> existing = new HashSet<>(parcelRepository.findAllTrackingNumbers());

        // 2) Фільтруємо дублі як з БД, так і дублікати у самому Excel
        List<Parcel> newParcels = dtoList.stream()
                .distinct() // захист від дубля в файлі
                .filter(dto -> !existing.contains(dto.getTrackingNumber())) // дублікати з БД
                .map(dto -> new Parcel(
                        dto.getTrackingNumber(),
                        dto.getZoneCode(),
                        dto.getRouteNumber(),
                        ParcelStatus.PENDING
                ))
                .toList();

        parcelRepository.saveAll(newParcels);
        return newParcels.size();
    }

    /**
     * Performs a scanning action for a parcel identified by its tracking number.
     *
     * <p>Depending on the parcel status, the system behaves as follows:
     * <ul>
     *     <li>If the parcel has never been scanned — it will be marked as {@code SCANNED} and persisted.</li>
     *     <li>If the parcel was already scanned — the system prevents duplicate processing
     *         and logs the repeated action in audit history.</li>
     * </ul>
     *
     * <p>In both cases, an audit event is recorded to maintain full traceability
     * of sorting operations and employee or device actions.
     *
     * @param trackingNumber Unique parcel identifier printed on the shipping label.
     * @param scannedBy      Identifier of the scanning device or employee (e.g. scanner ID, username).
     * @param sessionId      Unique identifier used to group scan events within the same workflow or shift.
     * @return {@link ScanResponse} describing the result of the scan (new scan or repeated scan).
     * @throws ParcelNotFoundException If no parcel is found matching the provided tracking number.
     */
    @Transactional
    public ScanResponse scanParcel(String trackingNumber, String scannedBy, String sessionId)
            throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ParcelNotFoundException(trackingNumber));

        // Avoid re-scan of an already scanned parcel
        if (parcel.getStatus() == ParcelStatus.SCANNED) {
            auditRepository.save(
                    AuditEvent.repeatedScan(parcel, scannedBy, sessionId).toEntity()
            );
            return ScanResponse.alreadyScanned(
                    parcel.getScannedAt(),
                    parcel.getScannedBy(),
                    parcel.getRouteNumber()
            );
        }

        parcel.setStatus(ParcelStatus.SCANNED); // indicates that the parcel has been scanned.
        parcel.setScannedAt(Instant.now()); //records the exact time of scanning.
        parcel.setScannedBy(scannedBy); //records who scanned.
        parcel.setUpdatedAt(Instant.now()); //updates the last modification time to track changes.
        parcelRepository.save(parcel); //saves changes to the DB.

        // Persist audit for success scan
        auditRepository.save(AuditEvent.successScan(parcel, scannedBy, sessionId)
                .toEntity());
        return ScanResponse.success(parcel.getRouteNumber());
    }

    /**
     * Retrieves audit entries for parcels scanned within a specific scanning session.
     *
     * <p>Common usage examples:
     * <ul>
     *     <li>Displaying scanned results for a specific employee shift.</li>
     *     <li>Tracking actions performed by a scanning device or workstation.</li>
     *     <li>Generating analytical or report data related to sorting activity.</li>
     * </ul>
     *
     * @param sessionId Identifier used to group scan events for a set of operations.
     * @return List of {@link ParcelAudit} entities matching the provided session ID.
     */
    public List<ParcelAudit> getScannedParcelsBySession(String sessionId) {
        return auditRepository.findBySessionId(sessionId);
    }

    public List<Parcel> getParcelsByStatus(ParcelStatus status) {
        return parcelRepository.findByStatus(status);
    }


    public void deleteAll() {
        auditRepository.deleteAll();
        parcelRepository.deleteAll();
        
    }
}
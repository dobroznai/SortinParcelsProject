package idhub.sortinparcels.service;


import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.exceptions.ParcelNotFoundException;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.repository.ParcelAuditRepository;
import idhub.sortinparcels.repository.ParcelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelAuditRepository auditRepository;


    /**
     * Scans a parcel and marks it as SCANNED if not scanned before.
     *
     * @param trackingNumber Unique tracking identifier of the parcel.
     * @param scannedBy      Identifier of the employee or device performing the scan.
     * @param sessionId      Session identifier for grouping scanned parcels within a scanning process.
     */
    @Transactional
    public ScanResponse scanParcel(String trackingNumber, String scannedBy, String sessionId) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber).orElseThrow(() -> new ParcelNotFoundException(trackingNumber));

        // Avoid re-scan of an already scanned parcel
        if (parcel.getStatus() == ParcelStatus.SCANNED) {
            return ScanResponse.alreadyScanned(parcel.getScannedAt(), parcel.getScannedBy(), parcel.getRouteNumber());
        }
        // Avoid scanning a delivered parcel
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            return ScanResponse.alreadyDelivered();
        }

        parcel.setStatus(ParcelStatus.SCANNED); // indicates that the parcel has been scanned.
        parcel.setScannedAt(Instant.now()); //records the exact time of scanning.
        parcel.setScannedBy(scannedBy); //records who scanned.
        parcel.setUpdatedAt(Instant.now()); //updates the last modification time to track changes.
        parcelRepository.save(parcel); //saves changes to the DB.

        return ScanResponse.success(parcel.getRouteNumber());
    }
}

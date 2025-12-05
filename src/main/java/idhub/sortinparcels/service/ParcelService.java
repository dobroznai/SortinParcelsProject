package idhub.sortinparcels.service;

import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.exceptions.ParcelNotFoundException;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.repository.ParcelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;


    /**
     *
     * @param trackingNumber The parcel number to be scanned.
     * @param scannerId      The ID of the scanner or user performing the scan.
     * @param sessionId      The ID of the scanning session (can be used for tracking or logging the session).
     */
    @Transactional
    public ScanResponse scanParcel(String trackingNumber, String scannerId, String sessionId) throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber).orElseThrow(() -> new ParcelNotFoundException(trackingNumber));

        //We prevent the same parcel from being scanned again.
        if (parcel.getStatus() == ParcelStatus.SCANNED) {
            return ScanResponse.alreadyScanned(parcel.getScannedAt(), parcel.getScannedBy(), parcel.getRouteNumber());
        }
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            return ScanResponse.alreadyDelivered();
        }

        parcel.setStatus(ParcelStatus.SCANNED); // indicates that the parcel has been scanned.
        parcel.setScannedAt(Instant.now()); //records the exact time of scanning.
        parcel.setScannedBy(scannerId); //records who scanned.
        parcel.setUpdatedAt(Instant.now()); //updates the last modification time to track changes.
        parcelRepository.save(parcel); //saves changes to the DB.

        return ScanResponse.success(parcel.getRouteNumber());
    }
}

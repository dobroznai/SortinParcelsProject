package idhub.sortinparcels.service;

import idhub.sortinparcels.dto.AuditEvent;
import idhub.sortinparcels.dto.report.ImportReport;
import idhub.sortinparcels.dto.ParcelReaderDto;
import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.exceptions.ParcelNotFoundException;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.model.ParcelAudit;
import idhub.sortinparcels.repository.ParcelAuditRepository;
import idhub.sortinparcels.repository.ParcelRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final ParcelAuditRepository auditRepository;
    private final Validator validator;

    /**
     * Imports parcels from pre-validated DTOs and creates a statistical report.
     * Steps:
     * 1. Validate DTOs using javax.validation.
     * 2. Detect duplicates within the file.
     * 3. Detect duplicates that already exist in DB.
     * 4. Persist only new parcels to the DB.
     *
     * @param dtoList raw data parsed from file
     * @return Import summary including duplicates and validation errors
     */
    @Transactional
    public ImportReport importParcelsFromDto(List<ParcelReaderDto> dtoList) {

        int totalRows = dtoList.size();

        // 1. Validation using javax.validation
        List<ParcelReaderDto> validDtos = dtoList.stream()
                .filter(dto -> validator.validate(dto).isEmpty())
                .toList();

        int invalidRows = totalRows - validDtos.size();

        // 2. Duplicates in the file itself
        int duplicatesInFile = Math.toIntExact(validDtos.size() - validDtos.stream().distinct().count());

        // 3. Duplicates in DB
        Set<String> existing = new HashSet<>(parcelRepository.findAllTrackingNumbers());
        int duplicatesInDb = (int) validDtos.stream()
                .distinct()
                .filter(dto -> existing.contains(dto.getTrackingNumber()))
                .count();

        // 4. New parcels to save
        List<Parcel> newParcels = validDtos.stream()
                .distinct()
                .filter(dto -> !existing.contains(dto.getTrackingNumber()))
                .map(dto -> new Parcel(
                        dto.getTrackingNumber(),
                        dto.getZoneCode(),
                        dto.getRouteNumber(),
                        ParcelStatus.PENDING
                ))
                .toList();

        parcelRepository.saveAll(newParcels);

        return ImportReport.builder()
                .totalRows(totalRows)
                .invalidRows(invalidRows)
                .duplicatesInFile(duplicatesInFile)
                .duplicatesInDb(duplicatesInDb)
                .imported(newParcels.size())
                .build();
    }

    @Transactional
    public ScanResponse scanParcel(String trackingNumber, String scannedBy, String sessionId)
            throws ParcelNotFoundException {
        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ParcelNotFoundException(trackingNumber));

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

        parcel.setStatus(ParcelStatus.SCANNED);
        parcel.setScannedAt(Instant.now());
        parcel.setScannedBy(scannedBy);
        parcel.setUpdatedAt(Instant.now());
        parcelRepository.save(parcel);

        auditRepository.save(AuditEvent.successScan(parcel, scannedBy, sessionId).toEntity());
        return ScanResponse.success(parcel.getRouteNumber());
    }

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
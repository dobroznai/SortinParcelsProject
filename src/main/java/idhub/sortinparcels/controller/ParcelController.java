package idhub.sortinparcels.controller;

import idhub.sortinparcels.dto.report.ImportReport;
import idhub.sortinparcels.dto.ParcelReaderDto;
import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.io.reader.DataReader;
import idhub.sortinparcels.io.reader.ReaderFactory;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.security.SortinParcelsSecurityUser;
import idhub.sortinparcels.service.ParcelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Parcel", description = "Operations on parcels: importing, scanning, querying lists")
@Slf4j
@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ReaderFactory readerFactory;
    private final ParcelService parcelService;

    // ============================= IMPORT =============================

    @Operation(
            summary = "Upload parcels from file",
            description = """
                Upload a file containing parcel data.
                Supported formats:
                - Excel (.xlsx, .xls)
                - Text (.txt), each line: <trackingNumber> <zoneCode> <routeNumber>
                
                ✔ User can upload and see report
                ✔ Admin can upload and see full report & statistics
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully"),
            @ApiResponse(responseCode = "400", description = "Unsupported file type or validation error")
    })
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ImportReport> uploadParcels(
            @RequestParam("file") MultipartFile file) {

        DataReader reader = readerFactory.getReader(file);
        List<ParcelReaderDto> dtoList = reader.read(file);

        ImportReport report = parcelService.importParcelsFromDto(dtoList);
        return ResponseEntity.ok(report);
    }


    // ============================= QUERIES =============================

    @Operation(summary = "Get all unscanned parcels (status=PENDING)")
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Parcel> getPendingParcels() {
        return parcelService.getParcelsByStatus(ParcelStatus.PENDING);
    }

    @Operation(summary = "Get all scanned parcels (status=SCANNED)")
    @GetMapping("/scanned")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Parcel> getScannedParcels() {
        return parcelService.getParcelsByStatus(ParcelStatus.SCANNED);
    }


    // ============================= SCANNING =============================

    @Operation(
            summary = "Scan a parcel by tracking number",
            description = """
                Marks a parcel as scanned and returns:
                - routeNumber
                - scannedAt timestamp
                - scannedBy username

                If parcel was already scanned → returns previous scan info.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcel scanned or already scanned"),
            @ApiResponse(responseCode = "404", description = "Parcel not found")
    })
    @PostMapping("/scan/{trackingNumber}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ScanResponse> scanParcel(
            @Parameter(description = "Tracking number to scan", example = "JD0146000065427282")
            @PathVariable String trackingNumber,
            @AuthenticationPrincipal SortinParcelsSecurityUser user
    ) {
        String scannedBy = user.getUsername();
        String sessionId = generateUserSession(user.getUsername());

        ScanResponse response = parcelService.scanParcel(
                trackingNumber,
                scannedBy,
                sessionId
        );
        return ResponseEntity.ok(response);
    }


    // ============================= ADMIN ONLY =============================

    @Operation(summary = "Clear ALL parcel and audit data (ADMIN ONLY)")
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearAll() {
        parcelService.deleteAll();
        log.warn("All parcels deleted!");
        return ResponseEntity.ok("Database cleared");
    }


    // ============================= PRIVATE SESSION GENERATOR =============================

    private String generateUserSession(String username) {
        return "SESSION-" + username + "-" + LocalDate.now();
    }
}
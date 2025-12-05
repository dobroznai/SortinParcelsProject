package idhub.sortinparcels.controller;

import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.exceptions.ParcelNotFoundException;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;
import idhub.sortinparcels.repository.ParcelRepository;
import idhub.sortinparcels.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Parcel", description = "Operations on parcels")
@Slf4j
@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelRepository parcelRepository;
    private final ExcelService excelService;

    @Operation(summary = "Upload parcels via Excel file",
            description = "Uploads an Excel file containing parcels. Each row should contain trackingNumber, gibitNumber, tourNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadParcels(
            @Parameter(description = "Excel file containing parcels")
            @RequestParam("file") MultipartFile file) {

        excelService.importParcels(file);
        return ResponseEntity.ok("Successfully uploaded " + parcelRepository.count() + " parcels");
    }

    @Operation(summary = "Get all unscanned parcels (status=PENDING)")
    @GetMapping("/pending")
    public List<Parcel> getPendingParcels() {
        return parcelRepository.findByStatus(ParcelStatus.PENDING);
    }

    @Operation(summary = "Get all scanned parcels (status=SCANNED)")
    @GetMapping("/scanned")
    public List<Parcel> getScannedParcels() {
        return parcelRepository.findByStatus(ParcelStatus.SCANNED);
    }

    @Operation(summary = "Scan parcel by tracking number",
            description = "Marks the parcel as scanned and returns information about the tour.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcel successfully scanned"),
            @ApiResponse(responseCode = "404", description = "Parcel not found")
    })
    @PostMapping("/scan/{trackingNumber}")
    public ResponseEntity<ScanResponse> scanParcel(
            @Parameter(description = "Tracking number of the parcel to scan")
            @PathVariable String trackingNumber) {

        Parcel parcel = parcelRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ParcelNotFoundException(trackingNumber));

        parcel.setStatus(ParcelStatus.SCANNED);
        parcelRepository.save(parcel);

        ScanResponse response = new ScanResponse(
                "Parcel scanned successfully",
                parcel.getRouteNumber()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Clear all parcels from database (for testing)")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAll() {
        parcelRepository.deleteAll();
        log.warn("All parcels deleted from database");
        return ResponseEntity.ok("Database cleared");
    }
}
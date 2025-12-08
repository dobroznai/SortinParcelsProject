package idhub.sortinparcels.controller;

import idhub.sortinparcels.dto.ParcelExcelDto;
import idhub.sortinparcels.dto.ScanResponse;
import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;

import idhub.sortinparcels.service.ExcelService;
import idhub.sortinparcels.service.ParcelService;
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

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Parcel", description = "Operations on parcels")
@Slf4j
@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ExcelService excelService;
    private final ParcelService parcelService;

    @Operation(summary = "Upload parcels via Excel file",
            description = "Uploads an Excel file containing parcels. Each row should contain trackingNumber, zoneCode, routeNumber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> uploadParcels(@RequestParam("file") MultipartFile file) {
        List<ParcelExcelDto> inputList = excelService.parseExcel(file);
        int importedCount = parcelService.importParcelsFromDto(inputList);
        return ResponseEntity.ok("Successfully uploaded " + importedCount + " parcels");
    }

    @Operation(summary = "Get all unscanned parcels (status=PENDING)")
    @GetMapping("/pending")
    public List<Parcel> getPendingParcels() {
        return parcelService.getParcelsByStatus(ParcelStatus.PENDING);
    }

    @Operation(summary = "Get all scanned parcels (status=SCANNED)")
    @GetMapping("/scanned")
    public List<Parcel> getScannedParcels() {
        return parcelService.getParcelsByStatus(ParcelStatus.SCANNED);
    }

    @Operation(
            summary = "Scan a parcel by tracking number",
            description = "Marks a parcel as scanned and returns route information. "
                    + "If the parcel was already scanned, no duplicate update occurs, "
                    + "but an audit record is still created."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcel successfully scanned or already scanned"),
            @ApiResponse(responseCode = "404", description = "Parcel not found")
    })
    @PostMapping("/scan/{trackingNumber}")
    public ResponseEntity<ScanResponse> scanParcel(
            @Parameter(description = "Tracking number of the parcel to scan", example = "JD0146000065427282")
            @PathVariable String trackingNumber

    ) {
        String scannedBy = getCurrentScanner(); // method that returns the scanner ID
        String sessionId = getCurrentSessionId(); // method that returns the current session
        ScanResponse response = parcelService.scanParcel(trackingNumber, scannedBy, sessionId);
        return ResponseEntity.ok(response);
    }

    // --- methods for automatic determination of scanner and session ---
    private String getCurrentScanner() {
        // can be taken from configuration, properties, or server logic
        return "AUTO-SCANNER-01";
    }

    private String getCurrentSessionId() {
        // for example, generate based on date/shift
        return "SHIFT-" + LocalDate.now() + "-AUTO";
    }

    @Operation(summary = "Clear all parcels from database (for testing)")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAll() {
        parcelService.deleteAll();
        log.warn("All parcels deleted from database");
        return ResponseEntity.ok("Database cleared");
    }
}
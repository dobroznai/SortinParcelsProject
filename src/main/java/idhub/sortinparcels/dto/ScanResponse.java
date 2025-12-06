package idhub.sortinparcels.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response after scanning a parcel")
public class ScanResponse {

    @Schema(description = "Message about the result of the operation", example = "Parcel scanned successfully")
    private String message;

    @Schema(description = "Tour number to which the parcel belongs", example = "225")
    private String routeNumber;

    @Schema(description = "Timestamp when parcel was scanned for sorting or delivery")
    private Instant scannedAt;

    @Schema(description = "User identifier (username or device ID) who scanned the parcel")
    private String scannedBy;

    /**
     * Response when parcel scanned successfully.
     */
    public static ScanResponse success(String routeNumber) {
        return new ScanResponse("Parcel scanned successfully", routeNumber, null, null);
    }
    /**
     * Response when scanning failed.
     */
    public static ScanResponse failure(String reason) {
        return new ScanResponse("Scan failed: " + reason, null, null, null  );
    }
    /**
     * Response when parcel already scanned previously.
     */
    public static ScanResponse alreadyScanned(Instant scannedAt, String scannedBy, String routeNumber) {
        return new ScanResponse("Parcel already scanned", routeNumber, scannedAt, scannedBy);
    }
    /**
     * Response when parcel was already delivered.
     */
    public static ScanResponse alreadyDelivered() {
        return new ScanResponse("Parcel already delivered", null, null, null);
    }


}
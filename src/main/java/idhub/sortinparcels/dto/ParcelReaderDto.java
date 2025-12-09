package idhub.sortinparcels.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode //for .distinct()
public class ParcelReaderDto {

    private String trackingNumber;
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "Zone format must be NN-NN")
    private String zoneCode;
    @Pattern(regexp = "\\d{3}", message = "Route must be 3 digits")
    private String routeNumber;
}
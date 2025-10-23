package idhub.sortinparcels.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(force = true)
@Table(name = "parcels")


public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private final String trackingNumber;
    @Column(nullable = false)
    private final String gibitNumber;
    @Column(nullable = false)
    private String tourNumber;
    @Column(nullable = false)
    private ParcelStatus status;

    public Parcel(String trackingNumber, String gibitNumber, String tourNumber, ParcelStatus status) {

        this.trackingNumber = trackingNumber;
        this.gibitNumber = gibitNumber;
        this.tourNumber = tourNumber;
        this.status = status;
    }
}

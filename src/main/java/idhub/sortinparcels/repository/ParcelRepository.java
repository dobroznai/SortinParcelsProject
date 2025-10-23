package idhub.sortinparcels.repository;

import idhub.sortinparcels.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    List<Parcel> getPendingParcels(String ParcelStatus);

    List<Parcel> findByTrackingNumber(String trackingNumber);

}

package idhub.sortinparcels.repository;

import idhub.sortinparcels.model.Parcel;
import idhub.sortinparcels.enums.ParcelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
    // Поиск посылки за trackingNumber
    // SELECT * FROM parcels WHERE trackingNumber = ? LIMIT 1
    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    // Получаем посылки по конкретному статусу(PENDING, SCANNED)
    List<Parcel> findByStatus(ParcelStatus status);

    // Получаем все посылки определенного тура
    List<Parcel> findByRouteNumber(String routeNumber);

    // Поиск посылки по зоне и маршруту (оптимизировано под @Index)
    List <Parcel> findByZoneCodeAndRouteNumber(String zoneCode, String routeNumber);

    // Проверка, существует ли посылка по конкретному trackingNumber
    boolean existsByTrackingNumber(String trackingNumber);

    // Вернуть количество удаленных строк, более безопаснее
    long deleteByStatus(ParcelStatus status);

}

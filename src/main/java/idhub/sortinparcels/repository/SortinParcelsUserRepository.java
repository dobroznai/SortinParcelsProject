package idhub.sortinparcels.repository;

import idhub.sortinparcels.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SortinParcelsUserRepository extends JpaRepository <User, Long>{
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}

package idhub.sortinparcels.repository.user;

import idhub.sortinparcels.enums.RoleStatus;
import idhub.sortinparcels.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByName(RoleStatus name);
}

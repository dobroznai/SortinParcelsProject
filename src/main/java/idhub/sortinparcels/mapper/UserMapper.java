package idhub.sortinparcels.mapper;

import idhub.sortinparcels.dto.users.UserAdminSummaryResponse;
import idhub.sortinparcels.dto.users.UserRegistrationResponse;
import idhub.sortinparcels.model.Role;
import idhub.sortinparcels.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(
            String username,
            String encodedPassword,
            Set<Role> roles
    ) {
        return new User(
                username,
                encodedPassword,
                roles
        );
    }

    public UserRegistrationResponse toUserRegistrationResponse(User user) {
        return new UserRegistrationResponse(
                user.getId(),
                user.getUsername()
        );
    }

    public UserAdminSummaryResponse toUserAdminSummaryResponse(User user) {
        return new UserAdminSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
    }
}

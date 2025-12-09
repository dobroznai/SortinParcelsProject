package idhub.sortinparcels.dto.users;

import java.util.Set;

public record UserAdminCreationRequest(
        String username,
        String email,
        String password,
        Set<String> roleNames
) {}
package idhub.sortinparcels.dto.user;

import java.util.Set;

public record UserAdminCreationRequest(
        String username,
        String password,
        Set<String> roleNames
) {}
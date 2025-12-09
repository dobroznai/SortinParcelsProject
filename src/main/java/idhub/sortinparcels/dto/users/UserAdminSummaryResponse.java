package idhub.sortinparcels.dto.users;

import java.util.Set;

public record UserAdminSummaryResponse(
        Long id,
        String username,
        Set<String> roles
) {}

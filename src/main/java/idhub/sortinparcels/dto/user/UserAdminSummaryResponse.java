package idhub.sortinparcels.dto.user;

import java.util.Set;

public record UserAdminSummaryResponse(
        Long id,
        String username,
        Set<String> roles
) {}

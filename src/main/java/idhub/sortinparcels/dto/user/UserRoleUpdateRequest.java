package idhub.sortinparcels.dto.user;

import java.util.Set;

public record UserRoleUpdateRequest(
        Set<String> roleNames
) { }

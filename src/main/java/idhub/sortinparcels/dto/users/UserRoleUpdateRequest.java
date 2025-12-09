package idhub.sortinparcels.dto.users;

import java.util.Set;

public record UserRoleUpdateRequest(
        Set<String> roleNames
) { }

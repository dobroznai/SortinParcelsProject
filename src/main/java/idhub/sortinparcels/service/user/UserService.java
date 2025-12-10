package idhub.sortinparcels.service.user;

import idhub.sortinparcels.dto.user.UserAdminCreationRequest;
import idhub.sortinparcels.dto.user.UserAdminSummaryResponse;
import idhub.sortinparcels.dto.user.UserRegistrationRequest;
import idhub.sortinparcels.dto.user.UserRegistrationResponse;
import idhub.sortinparcels.dto.user.UserRoleUpdateRequest;
import idhub.sortinparcels.enums.RoleStatus;
import idhub.sortinparcels.mapper.UserMapper;
import idhub.sortinparcels.model.Role;
import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.user.RoleRepository;
import idhub.sortinparcels.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with ID: " + userId));
    }

    private void validateUserExistence(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User already exists with name: " + username);
        }
    }

    private Role findRoleOrThrow(RoleStatus roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found with name: " + roleName));
    }

    private Set<Role> findRolesOrThrow(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> {
                    try {
                        RoleStatus roleStatus = RoleStatus.valueOf(name);
                        return findRoleOrThrow(roleStatus);
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Role not found with name: " + name);
                    }
                })
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserRegistrationResponse createUser(
            UserRegistrationRequest request,
            RoleStatus roleName
    ) {
        validateUserExistence(request.username());

        Role role = findRoleOrThrow(roleName);
        Set<Role> roles = Set.of(role);

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = userRepository.save(
                userMapper.toEntity(
                        request.username(),
                        encodedPassword,
                        roles
                ));

        return userMapper.toUserRegistrationResponse(newUser);
    }

    @Transactional
    public List<UserAdminSummaryResponse> getAllUsers() {
        log.info("Fetching all users for admin view");
        List<User> users = userRepository.findAll();
        log.info("Found {} users in system", users.size());
        return users.stream()
                .map(userMapper::toUserAdminSummaryResponse)
                .toList();
    }

    @Transactional
    public UserRegistrationResponse createAdminUser(
            UserAdminCreationRequest request
    ) {
        validateUserExistence(request.username());

        Set<Role> roles = findRolesOrThrow(request.roleNames());

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = userRepository.save(
                userMapper.toEntity(
                        request.username(),
                        encodedPassword,
                        roles
                ));
        return userMapper.toUserRegistrationResponse(newUser);
    }

    @Transactional
    public UserAdminSummaryResponse updateUserRoles(
            Long userId,
            UserRoleUpdateRequest request
    ) {
        User user = findUserOrThrow(userId);
        Set<Role> roles = findRolesOrThrow(request.roleNames());
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserAdminSummaryResponse(updatedUser);
    }
}

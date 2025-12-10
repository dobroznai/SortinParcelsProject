package idhub.sortinparcels.controller;




import idhub.sortinparcels.dto.user.UserRegistrationRequest;
import idhub.sortinparcels.dto.user.UserRegistrationResponse;
import idhub.sortinparcels.enums.RoleStatus;

import idhub.sortinparcels.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(
        name = "Authentication and User Management",
        description = "Endpoints for user registration and authentication (User - Employee)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    // POST /api/auth/register-artist – registration of a user with the USER role
    /**
     * POST /api/auth/register-user/
     * Registers a new user with the default role of USER
     *
     * @param userRegistrationRequest DTO containing username and password
     * @return the registration response with user details
     */
    @Operation(
            summary = "Register a new user/employee",
            description = "Registers a new user account and automatically assigns the USER role.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered and created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request (Validation failed)"),
                    @ApiResponse(responseCode = "404", description = "Role not found (Configuration error)"),
                    @ApiResponse(responseCode = "409", description = "Conflict (User with this username already exists)")
            }
    )
    @PostMapping("/register-user")
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserRegistrationResponse response =
                userService.createUser(userRegistrationRequest, RoleStatus.ROLE_USER);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }}
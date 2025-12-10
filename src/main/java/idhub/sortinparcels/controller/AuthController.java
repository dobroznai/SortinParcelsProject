package idhub.sortinparcels.controller;

import idhub.sortinparcels.auth.JwtResponse;
import idhub.sortinparcels.auth.LoginRequest;
import idhub.sortinparcels.dto.user.UserRegistrationRequest;
import idhub.sortinparcels.dto.user.UserRegistrationResponse;
import idhub.sortinparcels.enums.RoleStatus;
import idhub.sortinparcels.security.JwtService;
import idhub.sortinparcels.service.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication and User Management",
        description = "Endpoints for user registration and authentication (User - Employee)"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ---------- REGISTER USER ----------
    @Operation(
            summary = "Register a new user/employee",
            description = "Registers a new user account and automatically assigns the USER role.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Validation failed"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    @PostMapping("/register-user")
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @RequestBody UserRegistrationRequest userRegistrationRequest) {
        UserRegistrationResponse response =
                userService.createUser(userRegistrationRequest, RoleStatus.ROLE_USER);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ---------- LOGIN USER ----------
    @Operation(
            summary = "Login user and get JWT Token",
            description = "Authenticates user by username and password and returns JWT token."
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        return ResponseEntity.ok(new JwtResponse(token, role));
    }
}
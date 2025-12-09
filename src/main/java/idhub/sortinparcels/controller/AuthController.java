package idhub.sortinparcels.controller;



import idhub.sortinparcels.auth.JwtResponse;
import idhub.sortinparcels.auth.LoginRequest;
import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.SortinParcelsUserRepository;
import idhub.sortinparcels.security.JwtService;
import idhub.sortinparcels.security.SortinParcelsSecurityUser;
import idhub.sortinparcels.service.user.SortinParcelsUserDetailsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final AuthenticationManager authenticationManager;
    private final SortinParcelsUserDetailsService userDetailService;
    private final JwtService jwtService;
    private final SortinParcelsUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authRequest =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authResult = authenticationManager.authenticate(authRequest);

        UserDetails userDetails = userDetailService.loadUserByUsername(loginRequest.getUsername());

        String token = jwtService.generateToken((SortinParcelsSecurityUser) userDetails);

        User.Role role = jwtService.extractRole(token);

        return ResponseEntity.ok(new JwtResponse(token, role));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoleStatus(User.Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }
}
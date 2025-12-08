package idhub.sortinparcels.controller;



import idhub.sortinparcels.auth.JwtResponse;
import idhub.sortinparcels.auth.LoginRequest;
import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.SortinParcelsUserRepository;
import idhub.sortinparcels.security.JwtService;
import idhub.sortinparcels.security.SortinParcelsUserDetails;
import idhub.sortinparcels.service.SortinParcelsUserDetailService;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SortinParcelsUserDetailService userDetailService;
    private final JwtService jwtService;
    private final SortinParcelsUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthenticationManager authenticationManager, SortinParcelsUserDetailService userDetailService, JwtService jwtService, SortinParcelsUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailService = userDetailService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authRequest =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authResult = authenticationManager.authenticate(authRequest);

        UserDetails userDetails = userDetailService.loadUserByUsername(loginRequest.getUsername());

        String token = jwtService.generateToken((SortinParcelsUserDetails) userDetails);

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
        user.setRole(User.Role.USER);

        userRepository.save(user);

        return ResponseEntity.ok("User registered");
    }
}
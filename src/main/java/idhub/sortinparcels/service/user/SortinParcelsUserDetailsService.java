package idhub.sortinparcels.service.user;


import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.SortinParcelsUserRepository;
import idhub.sortinparcels.security.SortinParcelsSecurityUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SortinParcelsUserDetailsService implements UserDetailsService {

    private final SortinParcelsUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to load user by username='{}'", username);
        User SortinParcelUser = userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> {
                            log.warn("User with username='{}' not found", username);
                            return new UsernameNotFoundException
                                    ("User not found: " + username);
                        });

        return new SortinParcelsSecurityUser(SortinParcelUser);
    }
}
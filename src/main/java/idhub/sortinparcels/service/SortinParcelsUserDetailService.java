package idhub.sortinparcels.service;


import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.SortinParcelsUserRepository;
import idhub.sortinparcels.security.SortinParcelsUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SortinParcelsUserDetailService implements UserDetailsService {

    private SortinParcelsUserRepository sortinParcelsUserRepository;

    public SortinParcelsUserDetailService(SortinParcelsUserRepository sortinParcelsUserRepository) {
        this.sortinParcelsUserRepository = sortinParcelsUserRepository;
    }

    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User SortinParcelUser = sortinParcelsUserRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException
                        ("User not found: " + username));
        return new SortinParcelsUserDetails(SortinParcelUser);
    }
}
package idhub.sortinparcels.security;

import idhub.sortinparcels.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SortinParcelsUserDetails implements UserDetails {
    private final User user;

    public SortinParcelsUserDetails(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }


    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }


    public User.Role getRole() {
        return user.getRole();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

}
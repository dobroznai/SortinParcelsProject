package idhub.sortinparcels.config;

import idhub.sortinparcels.enums.RoleStatus;
import idhub.sortinparcels.model.User;
import idhub.sortinparcels.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class InitData {

    @Bean
    CommandLineRunner InitCommandLineRunner(UserRepository repository,
                                            PasswordEncoder encoder) {
        return args -> {
            if (repository.count() == 0) {
                User user = new User("user", encoder.encode("user123"), Set.of(RoleStatus.ROLE_USER), true);
                User admin = new User("admin", encoder.encode("admin123"), Set.of(RoleStatus.ROLE_ADMIN), true);

                repository.save(user);
                repository.save(admin);
            }
        };
    }
}
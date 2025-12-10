package idhub.sortinparcels.config;


import idhub.sortinparcels.security.JwtAuthFilter;
import idhub.sortinparcels.service.user.SortinParcelsUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final SortinParcelsUserDetailsService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(SortinParcelsUserDetailsService sortinParcelsUserDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(sortinParcelsUserDetailsService);
        authProvider.setUserDetailsService(sortinParcelsUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authenticationProvider)
            throws Exception {
        http.authenticationProvider(authenticationProvider);

        http.headers(headers -> headers
                .frameOptions(frame -> frame.disable()));

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Auth endpoints
                        .requestMatchers(
                                "/api/auth/**")
                        .permitAll()

                        // Swagger + H2 free
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**")
                        .permitAll()



                        // Parcel operations
                        .requestMatchers(
                                "/api/parcels/**")
                        .hasAnyRole("USER", "ADMIN")

                        // User profile
                        .requestMatchers(
                                "/api/user/**")
                        .hasRole("USER")

                        // Admin abilities
                        .requestMatchers(
                                "/api/admin/**",
                                "/api/audit/**")
                        .hasRole("ADMIN")

                        // Everything else requires auth
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(Customizer.withDefaults());
//        http.formLogin(Customizer.withDefaults());
        http.logout(Customizer.withDefaults());

        return http.build();
    }
}
package kr.ac.cbnu.tux.config;

import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    JwtAuthenticationFilter jwtRequestFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                            /* Community */
                    auth.requestMatchers(HttpMethod.POST, "/api/community").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/community/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/community/**").authenticated()

                            /* ReferenceRoom */
                            .requestMatchers("/api/referenceroom/**").hasAnyAuthority(
                                    UserRole.USER.name(), UserRole.MANAGER.name(), UserRole.ADMIN.name()
                            )

                            /* User */
                            .requestMatchers(HttpMethod.GET, "/api/auth").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/user/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/user/**").authenticated()
                            .requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())

                            .anyRequest().permitAll();
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package HD.educaze.config;

import HD.educaze.dto.Views.UserView;
import HD.educaze.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration @EnableMethodSecurity
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService users(AccountRepository accounts) {
        return username -> accounts.findByUsername(username.strip().toLowerCase(java.util.Locale.ROOT))
            .map(a -> User.withUsername(a.getUsername()).password(a.getPassword()).roles(a.getRole()).build())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid account"));
    }
    @Bean CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "X-CSRF-TOKEN"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**", config); return source;
    }
    @Bean SecurityFilterChain security(HttpSecurity http, AccountRepository accounts, ObjectMapper json, UserDetailsService users) throws Exception {
        http.cors(c -> {}).authorizeHttpRequests(a -> a
            .requestMatchers("/", "/index.html", "/assets/**", "/favicon.ico", "/login", "/dashboard", "/students", "/students/*", "/classes", "/classes/*", "/lecturers", "/courses", "/settings", "/error", "/api/auth/csrf").permitAll()
            .requestMatchers("/api/**", "/actuator/**", "/v3/**", "/swagger-ui/**", "/swagger-ui.html").authenticated()
            .anyRequest().denyAll())
            .formLogin(f -> f.loginPage("/login").loginProcessingUrl("/api/auth/login")
                .successHandler((request, response, authentication) -> {
                    var account = accounts.findByUsername(authentication.getName()).orElseThrow();
                    response.setContentType("application/json");
                    json.writeValue(response.getOutputStream(), new UserView(account.getUsername(), account.getDisplayName(), account.getRole()));
                })
                .failureHandler((request, response, error) -> { response.setStatus(401); response.setContentType("application/json"); json.writeValue(response.getOutputStream(), java.util.Map.of("message", "Email or password is incorrect.")); }).permitAll())
            .rememberMe(r -> r.key(java.util.UUID.randomUUID().toString()).userDetailsService(users).tokenValiditySeconds(7 * 24 * 3600))
            .logout(l -> l.logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID", "remember-me").logoutSuccessHandler((req, res, auth) -> res.setStatus(204)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, error) -> { res.setStatus(401); res.setContentType("application/json"); json.writeValue(res.getOutputStream(), java.util.Map.of("message", "Please sign in to continue.")); })
                .accessDeniedHandler((req, res, error) -> { res.setStatus(403); res.setContentType("application/json"); json.writeValue(res.getOutputStream(), java.util.Map.of("message", "You do not have permission, or your session token has expired. Please reload.")); }));
        return http.build();
    }
}

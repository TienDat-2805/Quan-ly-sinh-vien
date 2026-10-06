package HD.educaze.rest;
import HD.educaze.dto.Views.UserView;
import HD.educaze.repository.AccountRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AccountRepository accounts;
    private final PasswordEncoder encoder;
    public AuthController(AccountRepository accounts, PasswordEncoder encoder) { this.accounts = accounts; this.encoder = encoder; }
    @GetMapping("/csrf") public Map<String, String> csrf(CsrfToken token) { return Map.of("token", token.getToken(), "headerName", token.getHeaderName()); }
    @GetMapping("/me") public UserView me(Principal principal) { var a = accounts.findByUsername(principal.getName()).orElseThrow(); return new UserView(a.getUsername(), a.getDisplayName(), a.getRole()); }
    public record PasswordInput(@NotBlank String currentPassword, @NotBlank @Size(min = 8, max = 72) String newPassword) {}
    @PutMapping("/password") @Transactional public ResponseEntity<Void> password(Principal principal, @Valid @RequestBody PasswordInput input) {
        var account = accounts.findByUsername(principal.getName()).orElseThrow();
        if (!encoder.matches(input.currentPassword(), account.getPassword())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect.");
        account.setPassword(encoder.encode(input.newPassword())); accounts.save(account); return ResponseEntity.noContent().build();
    }
}

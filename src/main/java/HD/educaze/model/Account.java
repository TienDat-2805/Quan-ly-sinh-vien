package HD.educaze.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "accounts") @Getter @Setter
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 50) private String username;
    @Column(nullable = false, length = 100) private String password;
    @Column(name = "display_name", nullable = false, length = 100) private String displayName;
    @Column(nullable = false, length = 20) private String role;
}

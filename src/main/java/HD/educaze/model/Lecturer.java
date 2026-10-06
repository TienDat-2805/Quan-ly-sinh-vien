package HD.educaze.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity @Table(name = "lecturers") @Getter @Setter
public class Lecturer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(nullable = false, length = 20) private String phone;
    @Column(nullable = false, length = 120) private String faculty;
    @Column(nullable = false, length = 120) private String specialty;
    @Column(nullable = false, length = 15) private String status;
}

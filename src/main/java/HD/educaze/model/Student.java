package HD.educaze.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "students")
@Getter @Setter
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 10)
    private String gender;
    @Column(nullable = false)
    private LocalDate dob;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(nullable = false, length = 20)
    private String phone;
    @Column(length = 255)
    private String address;
    @Column(nullable = false, length = 15)
    private String status;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false)
    private AcademicClass academicClass;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @PrePersist void created() { createdAt = LocalDateTime.now(); }
}

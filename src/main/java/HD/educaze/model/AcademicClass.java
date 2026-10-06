package HD.educaze.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "academic_classes")
@Getter @Setter
public class AcademicClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 120)
    private String faculty;
    @Column(nullable = false, length = 100)
    private String advisor;
    @Column(nullable = false)
    private Integer cohort;
    @Column(nullable = false)
    private Integer capacity;
    @Column(name = "class_info_id", unique = true, length = 36)
    private String classInfoId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "subject_id")
    private Subject subject;
}

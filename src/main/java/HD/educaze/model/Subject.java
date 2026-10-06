package HD.educaze.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "subjects") @Getter @Setter
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 30) private String code;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false) private Integer credits;
    @Column(nullable = false, length = 120) private String faculty;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lecturer_id") private Lecturer lecturer;
}

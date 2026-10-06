package HD.educaze.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "semester"}))
@Getter @Setter
public class Grade {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id") private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "subject_id") private Subject subject;
    @Column(nullable = false, length = 30) private String semester;
    @Column(nullable = false, precision = 4, scale = 2) private BigDecimal score;
}

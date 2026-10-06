package HD.educaze.repository;

import HD.educaze.model.Grade;
import java.util.List;
import org.springframework.data.jpa.repository.*;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    @EntityGraph(attributePaths = {"student", "student.academicClass", "subject"})
    List<Grade> findAllByOrderByIdDesc();
    @EntityGraph(attributePaths = {"subject", "student"})
    List<Grade> findByStudentId(Long id);
    boolean existsByStudentIdAndSubjectIdAndSemester(Long studentId, Long subjectId, String semester);
    boolean existsBySubjectId(Long id);
    void deleteByStudentId(Long id);
}

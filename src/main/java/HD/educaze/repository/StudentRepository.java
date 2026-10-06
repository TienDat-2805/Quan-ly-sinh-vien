package HD.educaze.repository;

import HD.educaze.model.Student;
import org.springframework.data.jpa.repository.*;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByEmailIgnoreCase(String email);
    long countByAcademicClassId(Long classId);
    long countByStatus(String status);
}

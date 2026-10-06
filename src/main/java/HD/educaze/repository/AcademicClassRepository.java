package HD.educaze.repository;

import HD.educaze.model.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicClassRepository extends JpaRepository<AcademicClass, Long> {
    boolean existsByCodeIgnoreCase(String code);
}

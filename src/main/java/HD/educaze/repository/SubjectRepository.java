package HD.educaze.repository;

import HD.educaze.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    boolean existsByCodeIgnoreCase(String code);
}

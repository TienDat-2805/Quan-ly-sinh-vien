package HD.educaze.repository;
import HD.educaze.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    boolean existsByEmailIgnoreCase(String email);
}

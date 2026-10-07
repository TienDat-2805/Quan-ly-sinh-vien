package HD.educaze.repository;

import HD.educaze.model.Document;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository extends JpaRepository<Document, String> {
    List<Document> findByStudentIdAndDeletedFalseAndActiveTrueOrderByModificationTimeDescIdAsc(Long studentId);
    boolean existsByStudentId(Long studentId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Document d where d.id = :id and d.deleted = false and d.active = true")
    Optional<Document> findLiveForUpdate(@Param("id") String id);
}

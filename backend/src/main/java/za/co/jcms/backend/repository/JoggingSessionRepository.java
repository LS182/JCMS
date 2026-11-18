package za.co.jcms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.jcms.backend.entity.JoggingSession;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JoggingSessionRepository extends JpaRepository<JoggingSession, Integer> {

    List<JoggingSession> findAllByOrderBySessionDateDescStartTimeDesc();

    List<JoggingSession> findBySessionDateGreaterThanEqualOrderBySessionDateAscStartTimeAsc(LocalDate date);

    List<JoggingSession> findTop10BySessionDateGreaterThanEqualOrderBySessionDateAscStartTimeAsc(LocalDate date);

    List<JoggingSession> findByCategoryIgnoreCaseOrderBySessionDateAscStartTimeAsc(String category);

    List<JoggingSession> findByStatusIgnoreCaseOrderBySessionDateAscStartTimeAsc(String status);

    @Query("SELECT s FROM JoggingSession s WHERE s.category = :category AND s.sessionDate >= :date ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<JoggingSession> findUpcomingByCategory(@Param("category") String category, @Param("date") LocalDate date);

    long countBySessionDateGreaterThanEqual(LocalDate date);
}

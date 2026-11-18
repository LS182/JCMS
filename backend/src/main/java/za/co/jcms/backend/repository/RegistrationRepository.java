package za.co.jcms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.jcms.backend.entity.Registration;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Integer> {

    List<Registration> findBySession_SessionIdOrderByRegisteredAtAsc(Integer sessionId);

    List<Registration> findByMember_MemberIdOrderByRegisteredAtDesc(Integer memberId);

    Optional<Registration> findByMember_MemberIdAndSession_SessionId(Integer memberId, Integer sessionId);

    long countBySession_SessionIdAndStatus(Integer sessionId, String status);

    long countByStatus(String status);

    boolean existsByMember_MemberIdAndSession_SessionIdAndStatus(Integer memberId, Integer sessionId, String status);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.session.category = :category AND r.status = 'CONFIRMED'")
    long countConfirmedByCategory(@Param("category") String category);
}

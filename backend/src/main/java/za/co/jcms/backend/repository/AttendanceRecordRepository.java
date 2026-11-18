package za.co.jcms.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.jcms.backend.entity.AttendanceRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {

    List<AttendanceRecord> findByRegistration_Session_SessionId(Integer sessionId);

    List<AttendanceRecord> findByRegistration_Member_MemberId(Integer memberId);

    Optional<AttendanceRecord> findByRegistration_RegistrationId(Integer registrationId);

    boolean existsByRegistration_RegistrationId(Integer registrationId);
}

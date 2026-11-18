package za.co.jcms.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.jcms.backend.dto.request.AttendanceRequest;
import za.co.jcms.backend.dto.response.AttendanceResponse;
import za.co.jcms.backend.entity.AttendanceRecord;
import za.co.jcms.backend.entity.Registration;
import za.co.jcms.backend.exception.BadRequestException;
import za.co.jcms.backend.exception.ResourceNotFoundException;
import za.co.jcms.backend.repository.AttendanceRecordRepository;
import za.co.jcms.backend.repository.JoggingSessionRepository;
import za.co.jcms.backend.repository.MemberRepository;
import za.co.jcms.backend.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {

    public static final List<String> VALID_ATTENDANCE_STATUSES = Arrays.asList(
            "PRESENT",
            "ABSENT",
            "LATE"
    );

    private final AttendanceRecordRepository attendanceRepository;
    private final RegistrationRepository registrationRepository;
    private final JoggingSessionRepository sessionRepository;
    private final MemberRepository memberRepository;

    public AttendanceService(AttendanceRecordRepository attendanceRepository,
                             RegistrationRepository registrationRepository,
                             JoggingSessionRepository sessionRepository,
                             MemberRepository memberRepository) {
        this.attendanceRepository = attendanceRepository;
        this.registrationRepository = registrationRepository;
        this.sessionRepository = sessionRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceBySession(Integer sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session with ID " + sessionId + " not found");
        }
        return attendanceRepository.findByRegistration_Session_SessionId(sessionId).stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByMember(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " not found");
        }
        return attendanceRepository.findByRegistration_Member_MemberId(memberId).stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
    }

    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        if (request == null || request.getRegistrationId() == null) {
            throw new BadRequestException("Registration ID is required");
        }

        Registration registration = registrationRepository.findById(request.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration with ID " + request.getRegistrationId() + " not found"));

        if (!"CONFIRMED".equalsIgnoreCase(registration.getStatus())) {
            throw new BadRequestException("Cannot record attendance for a " + registration.getStatus() + " registration");
        }

        String status = matchStatus(request.getStatus());

        // Check if attendance is already recorded for this registration
        Optional<AttendanceRecord> existingOpt = attendanceRepository.findByRegistration_RegistrationId(request.getRegistrationId());

        AttendanceRecord record;
        if (existingOpt.isPresent()) {
            // Update existing record
            record = existingOpt.get();
            record.setStatus(status);
            record.setNotes(request.getNotes());
            record.setMarkedAt(LocalDateTime.now());
        } else {
            // Create new attendance record
            record = new AttendanceRecord(registration, status, request.getNotes());
        }

        AttendanceRecord saved = attendanceRepository.save(record);
        return new AttendanceResponse(saved);
    }

    private String matchStatus(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "PRESENT";
        }
        for (String st : VALID_ATTENDANCE_STATUSES) {
            if (st.equalsIgnoreCase(input.trim())) {
                return st;
            }
        }
        throw new BadRequestException("Invalid attendance status: '" + input + "'. Must be one of: " + VALID_ATTENDANCE_STATUSES);
    }
}

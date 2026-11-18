package za.co.jcms.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.jcms.backend.dto.request.RegistrationRequest;
import za.co.jcms.backend.dto.response.RegistrationResponse;
import za.co.jcms.backend.entity.JoggingSession;
import za.co.jcms.backend.entity.Member;
import za.co.jcms.backend.entity.Registration;
import za.co.jcms.backend.exception.BadRequestException;
import za.co.jcms.backend.exception.DuplicateResourceException;
import za.co.jcms.backend.exception.ResourceNotFoundException;
import za.co.jcms.backend.exception.SessionCapacityExceededException;
import za.co.jcms.backend.repository.JoggingSessionRepository;
import za.co.jcms.backend.repository.MemberRepository;
import za.co.jcms.backend.repository.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final MemberRepository memberRepository;
    private final JoggingSessionRepository sessionRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               MemberRepository memberRepository,
                               JoggingSessionRepository sessionRepository) {
        this.registrationRepository = registrationRepository;
        this.memberRepository = memberRepository;
        this.sessionRepository = sessionRepository;
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsBySession(Integer sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("Session with ID " + sessionId + " not found");
        }
        return registrationRepository.findBySession_SessionIdOrderByRegisteredAtAsc(sessionId).stream()
                .map(RegistrationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getRegistrationsByMember(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " not found");
        }
        return registrationRepository.findByMember_MemberIdOrderByRegisteredAtDesc(memberId).stream()
                .map(RegistrationResponse::new)
                .collect(Collectors.toList());
    }

    public RegistrationResponse registerMember(RegistrationRequest request) {
        if (request == null || request.getMemberId() == null || request.getSessionId() == null) {
            throw new BadRequestException("Both memberId and sessionId are required");
        }

        Integer memberId = request.getMemberId();
        Integer sessionId = request.getSessionId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member with ID " + memberId + " not found"));

        JoggingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + sessionId + " not found"));

        // Validate session availability
        if ("CANCELLED".equalsIgnoreCase(session.getStatus())) {
            throw new BadRequestException("Cannot register for session '" + session.getSessionName() + "' because it has been CANCELLED");
        }
        if ("COMPLETED".equalsIgnoreCase(session.getStatus())) {
            throw new BadRequestException("Cannot register for session '" + session.getSessionName() + "' because it is already COMPLETED");
        }

        // Check for existing registration record for this member and session
        Optional<Registration> existingOpt = registrationRepository.findByMember_MemberIdAndSession_SessionId(memberId, sessionId);

        if (existingOpt.isPresent()) {
            Registration existing = existingOpt.get();
            if ("CONFIRMED".equalsIgnoreCase(existing.getStatus())) {
                throw new DuplicateResourceException("Member '" + member.getFullName() +
                        "' is already confirmed for session '" + session.getSessionName() + "'");
            }
        }

        // Calculate active confirmed registrations
        long activeConfirmedCount = registrationRepository.countBySession_SessionIdAndStatus(sessionId, "CONFIRMED");
        if (activeConfirmedCount >= session.getCapacity()) {
            session.setStatus("FULL");
            sessionRepository.save(session);
            throw new SessionCapacityExceededException("Registration failed: Session '" + session.getSessionName() +
                    "' has reached full capacity (" + activeConfirmedCount + "/" + session.getCapacity() + ")");
        }

        Registration savedRegistration;
        if (existingOpt.isPresent()) {
            // Reactivate previously cancelled registration (preserves row, satisfies re-registration requirement)
            Registration toReactivate = existingOpt.get();
            toReactivate.setStatus("CONFIRMED");
            toReactivate.setCancelledAt(null);
            toReactivate.setUpdatedAt(LocalDateTime.now());
            savedRegistration = registrationRepository.save(toReactivate);
        } else {
            // Create fresh registration
            Registration newReg = new Registration(member, session, "CONFIRMED");
            savedRegistration = registrationRepository.save(newReg);
        }

        // If newly confirmed registration fills the session, mark as FULL
        if (activeConfirmedCount + 1 >= session.getCapacity()) {
            session.setStatus("FULL");
            sessionRepository.save(session);
        }

        return new RegistrationResponse(savedRegistration);
    }

    public RegistrationResponse cancelRegistration(Integer registrationId) {
        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration with ID " + registrationId + " not found"));

        if ("CANCELLED".equalsIgnoreCase(registration.getStatus())) {
            throw new BadRequestException("Registration with ID " + registrationId + " is already CANCELLED");
        }

        registration.setStatus("CANCELLED");
        registration.setCancelledAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());
        Registration saved = registrationRepository.save(registration);

        // If the session was marked FULL, reopen it since a spot was freed
        JoggingSession session = registration.getSession();
        if (session != null && "FULL".equalsIgnoreCase(session.getStatus())) {
            session.setStatus("OPEN");
            sessionRepository.save(session);
        }

        return new RegistrationResponse(saved);
    }
}

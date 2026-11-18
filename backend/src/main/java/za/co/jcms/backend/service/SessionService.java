package za.co.jcms.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.jcms.backend.dto.request.SessionRequest;
import za.co.jcms.backend.dto.response.SessionResponse;
import za.co.jcms.backend.entity.JoggingSession;
import za.co.jcms.backend.exception.BadRequestException;
import za.co.jcms.backend.exception.ResourceNotFoundException;
import za.co.jcms.backend.repository.JoggingSessionRepository;
import za.co.jcms.backend.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionService {

    public static final List<String> VALID_CATEGORIES = Arrays.asList(
            "Beginner Jogging",
            "Intermediate Running",
            "Advanced Running",
            "Family Fun Runs",
            "Marathon Training"
    );

    public static final List<String> VALID_STATUSES = Arrays.asList(
            "OPEN",
            "FULL",
            "CANCELLED",
            "COMPLETED"
    );

    private final JoggingSessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;

    public SessionService(JoggingSessionRepository sessionRepository, RegistrationRepository registrationRepository) {
        this.sessionRepository = sessionRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getAllSessions() {
        return sessionRepository.findAllByOrderBySessionDateDescStartTimeDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SessionResponse getSessionById(Integer id) {
        JoggingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + id + " not found"));
        return toResponse(session);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getUpcomingSessions() {
        return sessionRepository.findBySessionDateGreaterThanEqualOrderBySessionDateAscStartTimeAsc(LocalDate.now()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByCategory(String category) {
        return sessionRepository.findByCategoryIgnoreCaseOrderBySessionDateAscStartTimeAsc(category.trim()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByStatus(String status) {
        return sessionRepository.findByStatusIgnoreCaseOrderBySessionDateAscStartTimeAsc(status.trim()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SessionResponse createSession(SessionRequest request) {
        validateSessionRequest(request);

        String category = matchCategory(request.getCategory());
        String status = request.getStatus() != null && !request.getStatus().trim().isEmpty()
                ? matchStatus(request.getStatus())
                : "OPEN";

        JoggingSession session = new JoggingSession(
                request.getSessionName().trim(),
                request.getDescription().trim(),
                category,
                request.getSessionDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getLocation().trim(),
                request.getCapacity(),
                status
        );

        JoggingSession saved = sessionRepository.save(session);
        return toResponse(saved);
    }

    public SessionResponse updateSession(Integer id, SessionRequest request) {
        JoggingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + id + " not found"));

        validateSessionRequest(request);

        String category = matchCategory(request.getCategory());
        String status = request.getStatus() != null && !request.getStatus().trim().isEmpty()
                ? matchStatus(request.getStatus())
                : session.getStatus();

        long confirmedCount = registrationRepository.countBySession_SessionIdAndStatus(id, "CONFIRMED");
        if (request.getCapacity() < confirmedCount) {
            throw new BadRequestException("Capacity (" + request.getCapacity() +
                    ") cannot be less than currently confirmed registrations (" + confirmedCount + ")");
        }

        session.setSessionName(request.getSessionName().trim());
        session.setDescription(request.getDescription().trim());
        session.setCategory(category);
        session.setSessionDate(request.getSessionDate());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setLocation(request.getLocation().trim());
        session.setCapacity(request.getCapacity());
        session.setStatus(status);

        JoggingSession updated = sessionRepository.save(session);
        return toResponse(updated);
    }

    public void deleteSession(Integer id) {
        JoggingSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session with ID " + id + " not found"));

        long registrationCount = registrationRepository.findBySession_SessionIdOrderByRegisteredAtAsc(id).size();
        if (registrationCount > 0) {
            throw new BadRequestException("Cannot delete session with ID " + id +
                    " because it has " + registrationCount + " associated registration record(s). Change its status to CANCELLED instead.");
        }

        sessionRepository.delete(session);
    }

    public SessionResponse toResponse(JoggingSession session) {
        long confirmed = registrationRepository.countBySession_SessionIdAndStatus(session.getSessionId(), "CONFIRMED");
        return new SessionResponse(session, confirmed);
    }

    private void validateSessionRequest(SessionRequest request) {
        if (request == null) {
            throw new BadRequestException("Session data must not be empty");
        }
        if (request.getSessionName() == null || request.getSessionName().trim().isEmpty()) {
            throw new BadRequestException("Session name is required");
        }
        if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            throw new BadRequestException("Session description is required");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new BadRequestException("Session category is required");
        }
        matchCategory(request.getCategory()); // validates category

        if (request.getSessionDate() == null) {
            throw new BadRequestException("Session date is required");
        }
        if (request.getStartTime() == null) {
            throw new BadRequestException("Start time is required");
        }
        if (request.getEndTime() == null) {
            throw new BadRequestException("End time is required");
        }
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time (" + request.getEndTime() + ") must be after start time (" + request.getStartTime() + ")");
        }
        if (request.getLocation() == null || request.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location is required");
        }
        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new BadRequestException("Capacity must be a positive integer greater than 0");
        }
    }

    private String matchCategory(String input) {
        for (String cat : VALID_CATEGORIES) {
            if (cat.equalsIgnoreCase(input.trim())) {
                return cat;
            }
        }
        throw new BadRequestException("Invalid category: '" + input + "'. Must be one of: " + VALID_CATEGORIES);
    }

    private String matchStatus(String input) {
        for (String st : VALID_STATUSES) {
            if (st.equalsIgnoreCase(input.trim())) {
                return st;
            }
        }
        throw new BadRequestException("Invalid status: '" + input + "'. Must be one of: " + VALID_STATUSES);
    }
}

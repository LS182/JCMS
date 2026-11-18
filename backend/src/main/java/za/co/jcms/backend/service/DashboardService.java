package za.co.jcms.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.jcms.backend.dto.response.CategorySummaryResponse;
import za.co.jcms.backend.dto.response.DashboardResponse;
import za.co.jcms.backend.dto.response.SessionResponse;
import za.co.jcms.backend.entity.JoggingSession;
import za.co.jcms.backend.repository.JoggingSessionRepository;
import za.co.jcms.backend.repository.MemberRepository;
import za.co.jcms.backend.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberRepository memberRepository;
    private final JoggingSessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final SessionService sessionService;

    public DashboardService(MemberRepository memberRepository,
                            JoggingSessionRepository sessionRepository,
                            RegistrationRepository registrationRepository,
                            SessionService sessionService) {
        this.memberRepository = memberRepository;
        this.sessionRepository = sessionRepository;
        this.registrationRepository = registrationRepository;
        this.sessionService = sessionService;
    }

    public DashboardResponse getDashboardData() {
        LocalDate today = LocalDate.now();

        long totalMembers = memberRepository.count();
        long upcomingSessionsCount = sessionRepository.countBySessionDateGreaterThanEqual(today);
        long totalActiveBookings = registrationRepository.countByStatus("CONFIRMED");

        // Top 10 upcoming sessions
        List<JoggingSession> top10Upcoming = sessionRepository.findTop10BySessionDateGreaterThanEqualOrderBySessionDateAscStartTimeAsc(today);
        List<SessionResponse> upcomingResponses = top10Upcoming.stream()
                .map(sessionService::toResponse)
                .collect(Collectors.toList());

        // Group upcoming sessions by category
        Map<String, List<SessionResponse>> sessionsByCategory = new LinkedHashMap<>();
        for (String category : SessionService.VALID_CATEGORIES) {
            sessionsByCategory.put(category, new ArrayList<>());
        }
        for (SessionResponse sr : upcomingResponses) {
            sessionsByCategory.computeIfAbsent(sr.getCategory(), k -> new ArrayList<>()).add(sr);
        }

        // Category summaries for all 5 categories
        List<CategorySummaryResponse> categorySummaries = new ArrayList<>();
        for (String category : SessionService.VALID_CATEGORIES) {
            List<JoggingSession> allCategorySessions = sessionRepository.findByCategoryIgnoreCaseOrderBySessionDateAscStartTimeAsc(category);
            long totalSessions = allCategorySessions.size();
            long upcomingCatSessions = allCategorySessions.stream()
                    .filter(s -> !s.getSessionDate().isBefore(today))
                    .count();
            long totalCapacity = allCategorySessions.stream()
                    .mapToLong(JoggingSession::getCapacity)
                    .sum();
            long confirmedBookings = registrationRepository.countConfirmedByCategory(category);

            CategorySummaryResponse summary = new CategorySummaryResponse(
                    category,
                    totalSessions,
                    upcomingCatSessions,
                    totalCapacity,
                    confirmedBookings
            );
            summary.setSessions(sessionsByCategory.getOrDefault(category, Collections.emptyList()));
            categorySummaries.add(summary);
        }

        return new DashboardResponse(
                totalMembers,
                upcomingSessionsCount,
                totalActiveBookings,
                upcomingResponses,
                categorySummaries,
                sessionsByCategory
        );
    }
}

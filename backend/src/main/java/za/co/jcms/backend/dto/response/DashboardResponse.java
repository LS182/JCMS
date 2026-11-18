package za.co.jcms.backend.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private long totalMembers;
    private long upcomingSessionsCount;
    private long totalActiveBookings;
    private List<SessionResponse> upcomingSessions = new ArrayList<>();
    private List<CategorySummaryResponse> categorySummaries = new ArrayList<>();
    private Map<String, List<SessionResponse>> sessionsByCategory;

    public DashboardResponse() {
    }

    public DashboardResponse(long totalMembers, long upcomingSessionsCount, long totalActiveBookings,
                             List<SessionResponse> upcomingSessions, List<CategorySummaryResponse> categorySummaries,
                             Map<String, List<SessionResponse>> sessionsByCategory) {
        this.totalMembers = totalMembers;
        this.upcomingSessionsCount = upcomingSessionsCount;
        this.totalActiveBookings = totalActiveBookings;
        this.upcomingSessions = upcomingSessions;
        this.categorySummaries = categorySummaries;
        this.sessionsByCategory = sessionsByCategory;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getUpcomingSessionsCount() {
        return upcomingSessionsCount;
    }

    public void setUpcomingSessionsCount(long upcomingSessionsCount) {
        this.upcomingSessionsCount = upcomingSessionsCount;
    }

    public long getTotalActiveBookings() {
        return totalActiveBookings;
    }

    public void setTotalActiveBookings(long totalActiveBookings) {
        this.totalActiveBookings = totalActiveBookings;
    }

    public List<SessionResponse> getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(List<SessionResponse> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public List<CategorySummaryResponse> getCategorySummaries() {
        return categorySummaries;
    }

    public void setCategorySummaries(List<CategorySummaryResponse> categorySummaries) {
        this.categorySummaries = categorySummaries;
    }

    public Map<String, List<SessionResponse>> getSessionsByCategory() {
        return sessionsByCategory;
    }

    public void setSessionsByCategory(Map<String, List<SessionResponse>> sessionsByCategory) {
        this.sessionsByCategory = sessionsByCategory;
    }
}

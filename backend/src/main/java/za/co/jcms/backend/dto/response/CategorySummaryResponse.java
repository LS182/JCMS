package za.co.jcms.backend.dto.response;

import java.util.ArrayList;
import java.util.List;

public class CategorySummaryResponse {
    private String category;
    private long totalSessions;
    private long upcomingSessions;
    private long totalCapacity;
    private long totalConfirmedBookings;
    private List<SessionResponse> sessions = new ArrayList<>();

    public CategorySummaryResponse() {
    }

    public CategorySummaryResponse(String category, long totalSessions, long upcomingSessions, long totalCapacity, long totalConfirmedBookings) {
        this.category = category;
        this.totalSessions = totalSessions;
        this.upcomingSessions = upcomingSessions;
        this.totalCapacity = totalCapacity;
        this.totalConfirmedBookings = totalConfirmedBookings;
        this.sessions = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(long upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public long getTotalConfirmedBookings() {
        return totalConfirmedBookings;
    }

    public void setTotalConfirmedBookings(long totalConfirmedBookings) {
        this.totalConfirmedBookings = totalConfirmedBookings;
    }

    public List<SessionResponse> getSessions() {
        return sessions;
    }

    public void setSessions(List<SessionResponse> sessions) {
        this.sessions = sessions;
    }
}

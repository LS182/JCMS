package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDto implements Serializable {
    @SerializedName("totalMembers")
    private long totalMembers;

    @SerializedName("upcomingSessionsCount")
    private long upcomingSessionsCount;

    @SerializedName("totalActiveBookings")
    private long totalActiveBookings;

    @SerializedName("upcomingSessions")
    private List<SessionDto> upcomingSessions = new ArrayList<>();

    @SerializedName("categorySummaries")
    private List<CategorySummaryDto> categorySummaries = new ArrayList<>();

    @SerializedName("sessionsByCategory")
    private Map<String, List<SessionDto>> sessionsByCategory = new HashMap<>();

    public DashboardDto() {
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

    public List<SessionDto> getUpcomingSessions() {
        return upcomingSessions != null ? upcomingSessions : new ArrayList<>();
    }

    public void setUpcomingSessions(List<SessionDto> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public List<CategorySummaryDto> getCategorySummaries() {
        return categorySummaries != null ? categorySummaries : new ArrayList<>();
    }

    public void setCategorySummaries(List<CategorySummaryDto> categorySummaries) {
        this.categorySummaries = categorySummaries;
    }

    public Map<String, List<SessionDto>> getSessionsByCategory() {
        return sessionsByCategory != null ? sessionsByCategory : new HashMap<>();
    }

    public void setSessionsByCategory(Map<String, List<SessionDto>> sessionsByCategory) {
        this.sessionsByCategory = sessionsByCategory;
    }
}

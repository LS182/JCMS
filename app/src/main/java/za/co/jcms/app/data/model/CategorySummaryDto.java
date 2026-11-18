package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CategorySummaryDto implements Serializable {
    @SerializedName("category")
    private String category;

    @SerializedName("totalSessions")
    private long totalSessions;

    @SerializedName("upcomingSessions")
    private long upcomingSessions;

    @SerializedName("totalCapacity")
    private long totalCapacity;

    @SerializedName("totalConfirmedBookings")
    private long totalConfirmedBookings;

    @SerializedName("sessions")
    private List<SessionDto> sessions = new ArrayList<>();

    public CategorySummaryDto() {
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

    public List<SessionDto> getSessions() {
        return sessions != null ? sessions : new ArrayList<>();
    }

    public void setSessions(List<SessionDto> sessions) {
        this.sessions = sessions;
    }
}

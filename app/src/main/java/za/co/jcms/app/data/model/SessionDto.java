package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SessionDto implements Serializable {
    @SerializedName("sessionId")
    private Integer sessionId;

    @SerializedName("sessionName")
    private String sessionName;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("sessionDate")
    private String sessionDate;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("location")
    private String location;

    @SerializedName("capacity")
    private Integer capacity;

    @SerializedName("status")
    private String status;

    @SerializedName("confirmedParticipants")
    private long confirmedParticipants;

    @SerializedName("spotsRemaining")
    private long spotsRemaining;

    @SerializedName("createdAt")
    private String createdAt;

    public SessionDto() {
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(String sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity != null ? capacity : 0;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status != null ? status : "OPEN";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getConfirmedParticipants() {
        return confirmedParticipants;
    }

    public void setConfirmedParticipants(long confirmedParticipants) {
        this.confirmedParticipants = confirmedParticipants;
    }

    public long getSpotsRemaining() {
        return spotsRemaining;
    }

    public void setSpotsRemaining(long spotsRemaining) {
        this.spotsRemaining = spotsRemaining;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFull() {
        return "FULL".equalsIgnoreCase(status) || spotsRemaining <= 0;
    }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }
}

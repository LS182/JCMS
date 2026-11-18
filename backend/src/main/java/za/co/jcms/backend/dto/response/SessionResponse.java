package za.co.jcms.backend.dto.response;

import za.co.jcms.backend.entity.JoggingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SessionResponse {
    private Integer sessionId;
    private String sessionName;
    private String description;
    private String category;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Integer capacity;
    private String status;
    private long confirmedParticipants;
    private long spotsRemaining;
    private LocalDateTime createdAt;

    public SessionResponse() {
    }

    public SessionResponse(JoggingSession session, long confirmedParticipants) {
        if (session != null) {
            this.sessionId = session.getSessionId();
            this.sessionName = session.getSessionName();
            this.description = session.getDescription();
            this.category = session.getCategory();
            this.sessionDate = session.getSessionDate();
            this.startTime = session.getStartTime();
            this.endTime = session.getEndTime();
            this.location = session.getLocation();
            this.capacity = session.getCapacity();
            this.status = session.getStatus();
            this.confirmedParticipants = confirmedParticipants;
            this.spotsRemaining = Math.max(0, session.getCapacity() - confirmedParticipants);
            this.createdAt = session.getCreatedAt();
        }
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

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

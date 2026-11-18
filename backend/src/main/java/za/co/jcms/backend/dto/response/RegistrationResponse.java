package za.co.jcms.backend.dto.response;

import za.co.jcms.backend.entity.Registration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RegistrationResponse {
    private Integer registrationId;
    private Integer memberId;
    private String memberName;
    private String memberEmail;
    private Integer sessionId;
    private String sessionName;
    private String sessionCategory;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;

    public RegistrationResponse() {
    }

    public RegistrationResponse(Registration registration) {
        if (registration != null) {
            this.registrationId = registration.getRegistrationId();
            this.status = registration.getStatus();
            this.registeredAt = registration.getRegisteredAt();
            this.updatedAt = registration.getUpdatedAt();
            this.cancelledAt = registration.getCancelledAt();

            if (registration.getMember() != null) {
                this.memberId = registration.getMember().getMemberId();
                this.memberName = registration.getMember().getFullName();
                this.memberEmail = registration.getMember().getEmail();
            }

            if (registration.getSession() != null) {
                this.sessionId = registration.getSession().getSessionId();
                this.sessionName = registration.getSession().getSessionName();
                this.sessionCategory = registration.getSession().getCategory();
                this.sessionDate = registration.getSession().getSessionDate();
                this.startTime = registration.getSession().getStartTime();
            }
        }
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
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

    public String getSessionCategory() {
        return sessionCategory;
    }

    public void setSessionCategory(String sessionCategory) {
        this.sessionCategory = sessionCategory;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}

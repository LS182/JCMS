package za.co.jcms.backend.dto.response;

import za.co.jcms.backend.entity.AttendanceRecord;

import java.time.LocalDateTime;

public class AttendanceResponse {
    private Integer attendanceId;
    private Integer registrationId;
    private Integer memberId;
    private String memberName;
    private Integer sessionId;
    private String sessionName;
    private LocalDateTime markedAt;
    private String status;
    private String notes;

    public AttendanceResponse() {
    }

    public AttendanceResponse(AttendanceRecord record) {
        if (record != null) {
            this.attendanceId = record.getAttendanceId();
            this.markedAt = record.getMarkedAt();
            this.status = record.getStatus();
            this.notes = record.getNotes();

            if (record.getRegistration() != null) {
                this.registrationId = record.getRegistration().getRegistrationId();

                if (record.getRegistration().getMember() != null) {
                    this.memberId = record.getRegistration().getMember().getMemberId();
                    this.memberName = record.getRegistration().getMember().getFullName();
                }

                if (record.getRegistration().getSession() != null) {
                    this.sessionId = record.getRegistration().getSession().getSessionId();
                    this.sessionName = record.getRegistration().getSession().getSessionName();
                }
            }
        }
    }

    public Integer getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Integer attendanceId) {
        this.attendanceId = attendanceId;
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

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(LocalDateTime markedAt) {
        this.markedAt = markedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

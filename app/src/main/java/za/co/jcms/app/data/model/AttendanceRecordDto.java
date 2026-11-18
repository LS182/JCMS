package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AttendanceRecordDto implements Serializable {
    @SerializedName("attendanceId")
    private Integer attendanceId;

    @SerializedName("registrationId")
    private Integer registrationId;

    @SerializedName("memberId")
    private Integer memberId;

    @SerializedName("memberName")
    private String memberName;

    @SerializedName("sessionId")
    private Integer sessionId;

    @SerializedName("sessionName")
    private String sessionName;

    @SerializedName("markedAt")
    private String markedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    public AttendanceRecordDto() {
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

    public String getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(String markedAt) {
        this.markedAt = markedAt;
    }

    public String getStatus() {
        return status != null ? status : "PRESENT";
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

    public boolean isPresent() {
        return "PRESENT".equalsIgnoreCase(status);
    }

    public boolean isAbsent() {
        return "ABSENT".equalsIgnoreCase(status);
    }

    public boolean isLate() {
        return "LATE".equalsIgnoreCase(status);
    }
}

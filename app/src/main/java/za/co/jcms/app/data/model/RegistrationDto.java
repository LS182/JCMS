package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RegistrationDto implements Serializable {
    @SerializedName("registrationId")
    private Integer registrationId;

    @SerializedName("memberId")
    private Integer memberId;

    @SerializedName("memberName")
    private String memberName;

    @SerializedName("memberEmail")
    private String memberEmail;

    @SerializedName("sessionId")
    private Integer sessionId;

    @SerializedName("sessionName")
    private String sessionName;

    @SerializedName("sessionCategory")
    private String sessionCategory;

    @SerializedName("sessionDate")
    private String sessionDate;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("status")
    private String status;

    @SerializedName("registeredAt")
    private String registeredAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("cancelledAt")
    private String cancelledAt;

    public RegistrationDto() {
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

    public String getStatus() {
        return status != null ? status : "CONFIRMED";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }
}

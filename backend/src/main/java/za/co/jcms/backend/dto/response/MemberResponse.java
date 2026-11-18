package za.co.jcms.backend.dto.response;

import za.co.jcms.backend.entity.Member;

import java.time.LocalDateTime;

public class MemberResponse {
    private Integer memberId;
    private String fullName;
    private String email;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private LocalDateTime createdAt;

    public MemberResponse() {
    }

    public MemberResponse(Member member) {
        if (member != null) {
            this.memberId = member.getMemberId();
            this.fullName = member.getFullName();
            this.email = member.getEmail();
            this.contactNumber = member.getContactNumber();
            this.emergencyContactName = member.getEmergencyContactName();
            this.emergencyContactNumber = member.getEmergencyContactNumber();
            this.createdAt = member.getCreatedAt();
        }
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactNumber) {
        this.emergencyContactNumber = emergencyContactNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

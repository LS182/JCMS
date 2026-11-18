package za.co.jcms.backend.dto.request;

public class AttendanceRequest {
    private Integer registrationId;
    private String status;
    private String notes;

    public AttendanceRequest() {
    }

    public AttendanceRequest(Integer registrationId, String status, String notes) {
        this.registrationId = registrationId;
        this.status = status;
        this.notes = notes;
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
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

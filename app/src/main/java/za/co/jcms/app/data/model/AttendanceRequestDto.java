package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AttendanceRequestDto implements Serializable {
    @SerializedName("registrationId")
    private Integer registrationId;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    public AttendanceRequestDto() {
    }

    public AttendanceRequestDto(Integer registrationId, String status, String notes) {
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

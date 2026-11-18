package za.co.jcms.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class RegistrationRequestDto implements Serializable {
    @SerializedName("memberId")
    private Integer memberId;

    @SerializedName("sessionId")
    private Integer sessionId;

    public RegistrationRequestDto() {
    }

    public RegistrationRequestDto(Integer memberId, Integer sessionId) {
        this.memberId = memberId;
        this.sessionId = sessionId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }
}

package za.co.jcms.backend.dto.request;

public class RegistrationRequest {
    private Integer memberId;
    private Integer sessionId;

    public RegistrationRequest() {
    }

    public RegistrationRequest(Integer memberId, Integer sessionId) {
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

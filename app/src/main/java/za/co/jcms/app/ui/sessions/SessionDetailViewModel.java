package za.co.jcms.app.ui.sessions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.data.model.RegistrationRequestDto;
import za.co.jcms.app.data.model.SessionDto;
import za.co.jcms.app.data.model.SessionRequestDto;
import za.co.jcms.app.data.repository.MemberRepository;
import za.co.jcms.app.data.repository.RegistrationRepository;
import za.co.jcms.app.data.repository.SessionRepository;
import za.co.jcms.app.ui.common.Resource;

public class SessionDetailViewModel extends ViewModel {

    private final SessionRepository sessionRepository;
    private final RegistrationRepository registrationRepository;
    private final MemberRepository memberRepository;

    private final MediatorLiveData<Resource<SessionDto>> sessionDetail = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<RegistrationDto>>> registrations = new MediatorLiveData<>();

    public SessionDetailViewModel() {
        this.sessionRepository = new SessionRepository();
        this.registrationRepository = new RegistrationRepository();
        this.memberRepository = new MemberRepository();
    }

    public LiveData<Resource<SessionDto>> getSessionDetail() {
        return sessionDetail;
    }

    public LiveData<Resource<List<RegistrationDto>>> getRegistrations() {
        return registrations;
    }

    public void loadSession(int sessionId) {
        LiveData<Resource<SessionDto>> source = sessionRepository.getSessionById(sessionId);
        sessionDetail.addSource(source, value -> {
            sessionDetail.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                sessionDetail.removeSource(source);
            }
        });
    }

    public void loadRegistrations(int sessionId) {
        LiveData<Resource<List<RegistrationDto>>> source = registrationRepository.getRegistrationsBySession(sessionId);
        registrations.addSource(source, value -> {
            registrations.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                registrations.removeSource(source);
            }
        });
    }

    public LiveData<Resource<List<MemberDto>>> getAllMembers() {
        return memberRepository.getAllMembers();
    }

    public LiveData<Resource<RegistrationDto>> registerMember(RegistrationRequestDto request) {
        return registrationRepository.registerMemberForSession(request);
    }

    public LiveData<Resource<RegistrationDto>> cancelRegistration(int registrationId) {
        return registrationRepository.cancelRegistration(registrationId);
    }

    public LiveData<Resource<SessionDto>> updateSession(int id, SessionRequestDto request) {
        return sessionRepository.updateSession(id, request);
    }

    public LiveData<Resource<String>> deleteSession(int id) {
        return sessionRepository.deleteSession(id);
    }
}

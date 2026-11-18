package za.co.jcms.app.ui.members;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.MemberRequestDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.data.repository.MemberRepository;
import za.co.jcms.app.ui.common.Resource;

public class MemberDetailViewModel extends ViewModel {

    private final MemberRepository repository;
    private final MediatorLiveData<Resource<MemberDto>> memberDetail = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<RegistrationDto>>> memberHistory = new MediatorLiveData<>();

    public MemberDetailViewModel() {
        this.repository = new MemberRepository();
    }

    public LiveData<Resource<MemberDto>> getMemberDetail() {
        return memberDetail;
    }

    public LiveData<Resource<List<RegistrationDto>>> getMemberHistory() {
        return memberHistory;
    }

    public void loadMember(int memberId) {
        LiveData<Resource<MemberDto>> source = repository.getMemberById(memberId);
        memberDetail.addSource(source, value -> {
            memberDetail.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                memberDetail.removeSource(source);
            }
        });
    }

    public void loadHistory(int memberId) {
        LiveData<Resource<List<RegistrationDto>>> source = repository.getMemberRegistrations(memberId);
        memberHistory.addSource(source, value -> {
            memberHistory.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                memberHistory.removeSource(source);
            }
        });
    }

    public LiveData<Resource<MemberDto>> updateMember(int id, MemberRequestDto request) {
        return repository.updateMember(id, request);
    }
}

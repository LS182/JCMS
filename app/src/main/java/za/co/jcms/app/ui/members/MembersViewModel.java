package za.co.jcms.app.ui.members;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.MemberRequestDto;
import za.co.jcms.app.data.repository.MemberRepository;
import za.co.jcms.app.ui.common.Resource;

public class MembersViewModel extends ViewModel {

    private final MemberRepository repository;
    private final MediatorLiveData<Resource<List<MemberDto>>> rawMembers = new MediatorLiveData<>();
    private final MutableLiveData<List<MemberDto>> filteredMembers = new MutableLiveData<>();

    private String currentQuery = "";
    private List<MemberDto> currentList = new ArrayList<>();

    public MembersViewModel() {
        this.repository = new MemberRepository();
        loadMembers();
    }

    public LiveData<Resource<List<MemberDto>>> getRawMembers() {
        return rawMembers;
    }

    public LiveData<List<MemberDto>> getFilteredMembers() {
        return filteredMembers;
    }

    public void loadMembers() {
        LiveData<Resource<List<MemberDto>>> source = repository.getAllMembers();
        rawMembers.addSource(source, value -> {
            rawMembers.setValue(value);
            if (value != null && value.isSuccess() && value.getData() != null) {
                currentList = value.getData();
                applyFilter();
                rawMembers.removeSource(source);
            } else if (value != null && value.isError()) {
                rawMembers.removeSource(source);
            }
        });
    }

    public void setSearchQuery(String query) {
        this.currentQuery = query != null ? query.trim().toLowerCase() : "";
        applyFilter();
    }

    private void applyFilter() {
        if (currentList == null) {
            filteredMembers.setValue(new ArrayList<>());
            return;
        }

        if (currentQuery.isEmpty()) {
            filteredMembers.setValue(new ArrayList<>(currentList));
            return;
        }

        List<MemberDto> result = new ArrayList<>();
        for (MemberDto m : currentList) {
            boolean matchName = m.getFullName() != null && m.getFullName().toLowerCase().contains(currentQuery);
            boolean matchEmail = m.getEmail() != null && m.getEmail().toLowerCase().contains(currentQuery);
            if (matchName || matchEmail) {
                result.add(m);
            }
        }
        filteredMembers.setValue(result);
    }

    public LiveData<Resource<MemberDto>> registerMember(MemberRequestDto request) {
        return repository.registerMember(request);
    }
}

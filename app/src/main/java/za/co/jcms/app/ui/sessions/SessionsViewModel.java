package za.co.jcms.app.ui.sessions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import za.co.jcms.app.data.model.SessionDto;
import za.co.jcms.app.data.model.SessionRequestDto;
import za.co.jcms.app.data.repository.SessionRepository;
import za.co.jcms.app.ui.common.Resource;

public class SessionsViewModel extends ViewModel {

    private final SessionRepository repository;
    private final MediatorLiveData<Resource<List<SessionDto>>> rawSessions = new MediatorLiveData<>();
    private final MutableLiveData<List<SessionDto>> filteredSessions = new MutableLiveData<>();

    private String selectedCategory = "All";
    private String selectedStatus = "All";
    private List<SessionDto> currentList = new ArrayList<>();

    public SessionsViewModel() {
        this.repository = new SessionRepository();
        loadSessions();
    }

    public LiveData<Resource<List<SessionDto>>> getRawSessions() {
        return rawSessions;
    }

    public LiveData<List<SessionDto>> getFilteredSessions() {
        return filteredSessions;
    }

    public void loadSessions() {
        LiveData<Resource<List<SessionDto>>> source = repository.getAllSessions();
        rawSessions.addSource(source, value -> {
            rawSessions.setValue(value);
            if (value != null && value.isSuccess() && value.getData() != null) {
                currentList = value.getData();
                applyFilter();
                rawSessions.removeSource(source);
            } else if (value != null && value.isError()) {
                rawSessions.removeSource(source);
            }
        });
    }

    public void setCategoryFilter(String category) {
        this.selectedCategory = category;
        applyFilter();
    }

    public void setStatusFilter(String status) {
        this.selectedStatus = status;
        applyFilter();
    }

    private void applyFilter() {
        if (currentList == null) {
            filteredSessions.setValue(new ArrayList<>());
            return;
        }

        List<SessionDto> result = new ArrayList<>();
        for (SessionDto s : currentList) {
            boolean matchCategory = "All".equalsIgnoreCase(selectedCategory)
                    || (s.getCategory() != null && s.getCategory().equalsIgnoreCase(selectedCategory));
            boolean matchStatus = "All".equalsIgnoreCase(selectedStatus)
                    || (s.getStatus() != null && s.getStatus().equalsIgnoreCase(selectedStatus));

            if (matchCategory && matchStatus) {
                result.add(s);
            }
        }
        filteredSessions.setValue(result);
    }

    public LiveData<Resource<SessionDto>> createSession(SessionRequestDto request) {
        return repository.createSession(request);
    }
}

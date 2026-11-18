package za.co.jcms.app.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import za.co.jcms.app.data.model.DashboardDto;
import za.co.jcms.app.data.repository.DashboardRepository;
import za.co.jcms.app.ui.common.Resource;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;
    private final MediatorLiveData<Resource<DashboardDto>> dashboardData = new MediatorLiveData<>();

    public DashboardViewModel() {
        this.repository = new DashboardRepository();
        loadDashboard();
    }

    public LiveData<Resource<DashboardDto>> getDashboardData() {
        return dashboardData;
    }

    public void loadDashboard() {
        LiveData<Resource<DashboardDto>> source = repository.getDashboardData();
        dashboardData.addSource(source, value -> {
            dashboardData.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                dashboardData.removeSource(source);
            }
        });
    }
}

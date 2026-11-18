package za.co.jcms.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.jcms.app.api.ApiClient;
import za.co.jcms.app.api.JcmsApiService;
import za.co.jcms.app.data.model.DashboardDto;
import za.co.jcms.app.ui.common.Resource;

public class DashboardRepository {

    private final JcmsApiService apiService;

    public DashboardRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<DashboardDto>> getDashboardData() {
        MutableLiveData<Resource<DashboardDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getDashboard().enqueue(new Callback<DashboardDto>() {
            @Override
            public void onResponse(Call<DashboardDto> call, Response<DashboardDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    String error = ApiClient.parseErrorMessage(response);
                    liveData.setValue(Resource.error(error));
                }
            }

            @Override
            public void onFailure(Call<DashboardDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + (t.getMessage() != null ? t.getMessage() : "Unable to reach server")));
            }
        });

        return liveData;
    }
}

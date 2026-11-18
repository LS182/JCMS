package za.co.jcms.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.jcms.app.api.ApiClient;
import za.co.jcms.app.api.JcmsApiService;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.data.model.RegistrationRequestDto;
import za.co.jcms.app.ui.common.Resource;

import java.util.List;

public class RegistrationRepository {

    private final JcmsApiService apiService;

    public RegistrationRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<RegistrationDto>>> getRegistrationsBySession(int sessionId) {
        MutableLiveData<Resource<List<RegistrationDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getRegistrationsBySession(sessionId).enqueue(new Callback<List<RegistrationDto>>() {
            @Override
            public void onResponse(Call<List<RegistrationDto>> call, Response<List<RegistrationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<RegistrationDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<RegistrationDto>>> getRegistrationsByMember(int memberId) {
        MutableLiveData<Resource<List<RegistrationDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getRegistrationsByMember(memberId).enqueue(new Callback<List<RegistrationDto>>() {
            @Override
            public void onResponse(Call<List<RegistrationDto>> call, Response<List<RegistrationDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<RegistrationDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<RegistrationDto>> registerMemberForSession(RegistrationRequestDto request) {
        MutableLiveData<Resource<RegistrationDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.registerMemberForSession(request).enqueue(new Callback<RegistrationDto>() {
            @Override
            public void onResponse(Call<RegistrationDto> call, Response<RegistrationDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<RegistrationDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<RegistrationDto>> cancelRegistration(int registrationId) {
        MutableLiveData<Resource<RegistrationDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.cancelRegistration(registrationId).enqueue(new Callback<RegistrationDto>() {
            @Override
            public void onResponse(Call<RegistrationDto> call, Response<RegistrationDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<RegistrationDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }
}

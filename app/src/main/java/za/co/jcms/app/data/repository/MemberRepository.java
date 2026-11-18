package za.co.jcms.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.jcms.app.api.ApiClient;
import za.co.jcms.app.api.JcmsApiService;
import za.co.jcms.app.data.model.MemberDto;
import za.co.jcms.app.data.model.MemberRequestDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.ui.common.Resource;

import java.util.List;

public class MemberRepository {

    private final JcmsApiService apiService;

    public MemberRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<MemberDto>>> getAllMembers() {
        MutableLiveData<Resource<List<MemberDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getAllMembers().enqueue(new Callback<List<MemberDto>>() {
            @Override
            public void onResponse(Call<List<MemberDto>> call, Response<List<MemberDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<MemberDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<MemberDto>> getMemberById(int id) {
        MutableLiveData<Resource<MemberDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getMemberById(id).enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<MemberDto>> registerMember(MemberRequestDto request) {
        MutableLiveData<Resource<MemberDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.registerMember(request).enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<MemberDto>> updateMember(int id, MemberRequestDto request) {
        MutableLiveData<Resource<MemberDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.updateMember(id, request).enqueue(new Callback<MemberDto>() {
            @Override
            public void onResponse(Call<MemberDto> call, Response<MemberDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<MemberDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<RegistrationDto>>> getMemberRegistrations(int memberId) {
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
}

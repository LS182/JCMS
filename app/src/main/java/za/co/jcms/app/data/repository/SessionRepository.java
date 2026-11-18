package za.co.jcms.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.jcms.app.api.ApiClient;
import za.co.jcms.app.api.JcmsApiService;
import za.co.jcms.app.data.model.SessionDto;
import za.co.jcms.app.data.model.SessionRequestDto;
import za.co.jcms.app.ui.common.Resource;

import java.util.List;
import java.util.Map;

public class SessionRepository {

    private final JcmsApiService apiService;

    public SessionRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<SessionDto>>> getAllSessions() {
        MutableLiveData<Resource<List<SessionDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getAllSessions().enqueue(new Callback<List<SessionDto>>() {
            @Override
            public void onResponse(Call<List<SessionDto>> call, Response<List<SessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<SessionDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<SessionDto>>> getUpcomingSessions() {
        MutableLiveData<Resource<List<SessionDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getUpcomingSessions().enqueue(new Callback<List<SessionDto>>() {
            @Override
            public void onResponse(Call<List<SessionDto>> call, Response<List<SessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<SessionDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<SessionDto>> getSessionById(int id) {
        MutableLiveData<Resource<SessionDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getSessionById(id).enqueue(new Callback<SessionDto>() {
            @Override
            public void onResponse(Call<SessionDto> call, Response<SessionDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<SessionDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<SessionDto>>> getSessionsByCategory(String category) {
        MutableLiveData<Resource<List<SessionDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getSessionsByCategory(category).enqueue(new Callback<List<SessionDto>>() {
            @Override
            public void onResponse(Call<List<SessionDto>> call, Response<List<SessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<SessionDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<SessionDto>> createSession(SessionRequestDto request) {
        MutableLiveData<Resource<SessionDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.createSession(request).enqueue(new Callback<SessionDto>() {
            @Override
            public void onResponse(Call<SessionDto> call, Response<SessionDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<SessionDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<SessionDto>> updateSession(int id, SessionRequestDto request) {
        MutableLiveData<Resource<SessionDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.updateSession(id, request).enqueue(new Callback<SessionDto>() {
            @Override
            public void onResponse(Call<SessionDto> call, Response<SessionDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<SessionDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<String>> deleteSession(int id) {
        MutableLiveData<Resource<String>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.deleteSession(id).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(Resource.success("Session deleted successfully"));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }
}

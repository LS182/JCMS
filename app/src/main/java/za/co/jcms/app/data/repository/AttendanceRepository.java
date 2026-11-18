package za.co.jcms.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import za.co.jcms.app.api.ApiClient;
import za.co.jcms.app.api.JcmsApiService;
import za.co.jcms.app.data.model.AttendanceRecordDto;
import za.co.jcms.app.data.model.AttendanceRequestDto;
import za.co.jcms.app.ui.common.Resource;

import java.util.List;

public class AttendanceRepository {

    private final JcmsApiService apiService;

    public AttendanceRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Resource<List<AttendanceRecordDto>>> getAttendanceBySession(int sessionId) {
        MutableLiveData<Resource<List<AttendanceRecordDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getAttendanceBySession(sessionId).enqueue(new Callback<List<AttendanceRecordDto>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecordDto>> call, Response<List<AttendanceRecordDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecordDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<AttendanceRecordDto>>> getAttendanceByMember(int memberId) {
        MutableLiveData<Resource<List<AttendanceRecordDto>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.getAttendanceByMember(memberId).enqueue(new Callback<List<AttendanceRecordDto>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecordDto>> call, Response<List<AttendanceRecordDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceRecordDto>> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Resource<AttendanceRecordDto>> recordAttendance(AttendanceRequestDto request) {
        MutableLiveData<Resource<AttendanceRecordDto>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading());

        apiService.recordAttendance(request).enqueue(new Callback<AttendanceRecordDto>() {
            @Override
            public void onResponse(Call<AttendanceRecordDto> call, Response<AttendanceRecordDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(Resource.success(response.body()));
                } else {
                    liveData.setValue(Resource.error(ApiClient.parseErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<AttendanceRecordDto> call, Throwable t) {
                liveData.setValue(Resource.error("Network error: " + t.getMessage()));
            }
        });

        return liveData;
    }
}

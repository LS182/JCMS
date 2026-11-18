package za.co.jcms.app.ui.attendance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import za.co.jcms.app.data.model.AttendanceRecordDto;
import za.co.jcms.app.data.model.AttendanceRequestDto;
import za.co.jcms.app.data.model.RegistrationDto;
import za.co.jcms.app.data.repository.AttendanceRepository;
import za.co.jcms.app.data.repository.RegistrationRepository;
import za.co.jcms.app.ui.common.Resource;

public class AttendanceViewModel extends ViewModel {

    private final AttendanceRepository attendanceRepository;
    private final RegistrationRepository registrationRepository;

    private final MediatorLiveData<Resource<List<RegistrationDto>>> registrations = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<AttendanceRecordDto>>> attendanceRecords = new MediatorLiveData<>();

    public AttendanceViewModel() {
        this.attendanceRepository = new AttendanceRepository();
        this.registrationRepository = new RegistrationRepository();
    }

    public LiveData<Resource<List<RegistrationDto>>> getRegistrations() {
        return registrations;
    }

    public LiveData<Resource<List<AttendanceRecordDto>>> getAttendanceRecords() {
        return attendanceRecords;
    }

    public void loadData(int sessionId) {
        LiveData<Resource<List<RegistrationDto>>> regSource = registrationRepository.getRegistrationsBySession(sessionId);
        registrations.addSource(regSource, value -> {
            registrations.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                registrations.removeSource(regSource);
            }
        });

        LiveData<Resource<List<AttendanceRecordDto>>> attSource = attendanceRepository.getAttendanceBySession(sessionId);
        attendanceRecords.addSource(attSource, value -> {
            attendanceRecords.setValue(value);
            if (value != null && (value.isSuccess() || value.isError())) {
                attendanceRecords.removeSource(attSource);
            }
        });
    }

    public LiveData<Resource<AttendanceRecordDto>> recordAttendance(AttendanceRequestDto request) {
        return attendanceRepository.recordAttendance(request);
    }
}

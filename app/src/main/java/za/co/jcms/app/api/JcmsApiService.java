package za.co.jcms.app.api;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;
import za.co.jcms.app.data.model.*;

public interface JcmsApiService {

    // ==================== DASHBOARD ====================
    @GET("dashboard")
    Call<DashboardDto> getDashboard();

    // ==================== SESSIONS ====================
    @GET("sessions")
    Call<List<SessionDto>> getAllSessions();

    @GET("sessions/upcoming")
    Call<List<SessionDto>> getUpcomingSessions();

    @GET("sessions/{id}")
    Call<SessionDto> getSessionById(@Path("id") int id);

    @GET("sessions/category/{category}")
    Call<List<SessionDto>> getSessionsByCategory(@Path("category") String category);

    @POST("sessions")
    Call<SessionDto> createSession(@Body SessionRequestDto request);

    @PUT("sessions/{id}")
    Call<SessionDto> updateSession(@Path("id") int id, @Body SessionRequestDto request);

    @DELETE("sessions/{id}")
    Call<Map<String, String>> deleteSession(@Path("id") int id);

    // ==================== MEMBERS ====================
    @GET("members")
    Call<List<MemberDto>> getAllMembers();

    @GET("members/{id}")
    Call<MemberDto> getMemberById(@Path("id") int id);

    @POST("members")
    Call<MemberDto> registerMember(@Body MemberRequestDto request);

    @PUT("members/{id}")
    Call<MemberDto> updateMember(@Path("id") int id, @Body MemberRequestDto request);

    // ==================== REGISTRATIONS ====================
    @GET("registrations/session/{sessionId}")
    Call<List<RegistrationDto>> getRegistrationsBySession(@Path("sessionId") int sessionId);

    @GET("registrations/member/{memberId}")
    Call<List<RegistrationDto>> getRegistrationsByMember(@Path("memberId") int memberId);

    @POST("registrations")
    Call<RegistrationDto> registerMemberForSession(@Body RegistrationRequestDto request);

    @PUT("registrations/{id}/cancel")
    Call<RegistrationDto> cancelRegistration(@Path("id") int id);

    // ==================== ATTENDANCE ====================
    @GET("attendance/session/{sessionId}")
    Call<List<AttendanceRecordDto>> getAttendanceBySession(@Path("sessionId") int sessionId);

    @GET("attendance/member/{memberId}")
    Call<List<AttendanceRecordDto>> getAttendanceByMember(@Path("memberId") int memberId);

    @POST("attendance")
    Call<AttendanceRecordDto> recordAttendance(@Body AttendanceRequestDto request);
}

package za.co.jcms.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.jcms.backend.dto.request.AttendanceRequest;
import za.co.jcms.backend.dto.response.AttendanceResponse;
import za.co.jcms.backend.service.AttendanceService;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceBySession(@PathVariable("sessionId") Integer sessionId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySession(sessionId));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByMember(@PathVariable("memberId") Integer memberId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> recordAttendance(@RequestBody AttendanceRequest request) {
        AttendanceResponse response = attendanceService.recordAttendance(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

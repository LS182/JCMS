package za.co.jcms.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.jcms.backend.dto.request.SessionRequest;
import za.co.jcms.backend.dto.response.SessionResponse;
import za.co.jcms.backend.service.SessionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessions() {
        return ResponseEntity.ok(sessionService.getUpcomingSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SessionResponse>> getSessionsByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(sessionService.getSessionsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionRequest request) {
        SessionResponse created = sessionService.createSession(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionResponse> updateSession(@PathVariable("id") Integer id,
                                                         @RequestBody SessionRequest request) {
        SessionResponse updated = sessionService.updateSession(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSession(@PathVariable("id") Integer id) {
        sessionService.deleteSession(id);
        return ResponseEntity.ok(Map.of("message", "Session with ID " + id + " was deleted successfully"));
    }
}

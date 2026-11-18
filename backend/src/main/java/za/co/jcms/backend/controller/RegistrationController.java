package za.co.jcms.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.jcms.backend.dto.request.RegistrationRequest;
import za.co.jcms.backend.dto.response.RegistrationResponse;
import za.co.jcms.backend.service.RegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<RegistrationResponse>> getRegistrationsBySession(@PathVariable("sessionId") Integer sessionId) {
        return ResponseEntity.ok(registrationService.getRegistrationsBySession(sessionId));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<RegistrationResponse>> getRegistrationsByMember(@PathVariable("memberId") Integer memberId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<RegistrationResponse> registerMember(@RequestBody RegistrationRequest request) {
        RegistrationResponse response = registrationService.registerMember(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<RegistrationResponse> cancelRegistration(@PathVariable("id") Integer id) {
        RegistrationResponse response = registrationService.cancelRegistration(id);
        return ResponseEntity.ok(response);
    }
}

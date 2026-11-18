package za.co.jcms.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.jcms.backend.dto.request.MemberRequest;
import za.co.jcms.backend.dto.response.MemberResponse;
import za.co.jcms.backend.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> registerMember(@RequestBody MemberRequest request) {
        MemberResponse created = memberService.registerMember(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("id") Integer id,
                                                       @RequestBody MemberRequest request) {
        MemberResponse updated = memberService.updateMember(id, request);
        return ResponseEntity.ok(updated);
    }
}

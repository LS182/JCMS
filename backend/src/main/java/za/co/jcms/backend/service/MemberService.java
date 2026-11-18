package za.co.jcms.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.jcms.backend.dto.request.MemberRequest;
import za.co.jcms.backend.dto.response.MemberResponse;
import za.co.jcms.backend.dto.response.RegistrationResponse;
import za.co.jcms.backend.entity.Member;
import za.co.jcms.backend.entity.Registration;
import za.co.jcms.backend.exception.BadRequestException;
import za.co.jcms.backend.exception.DuplicateResourceException;
import za.co.jcms.backend.exception.ResourceNotFoundException;
import za.co.jcms.backend.repository.MemberRepository;
import za.co.jcms.backend.repository.RegistrationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RegistrationRepository registrationRepository;

    public MemberService(MemberRepository memberRepository, RegistrationRepository registrationRepository) {
        this.memberRepository = memberRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Integer id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member with ID " + id + " not found"));
        return new MemberResponse(member);
    }

    public MemberResponse registerMember(MemberRequest request) {
        validateMemberRequest(request);

        String email = request.getEmail().trim().toLowerCase();
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Member with email '" + email + "' already exists");
        }

        Member member = new Member(
                request.getFullName().trim(),
                email,
                request.getContactNumber().trim(),
                request.getEmergencyContactName().trim(),
                request.getEmergencyContactNumber().trim()
        );

        Member saved = memberRepository.save(member);
        return new MemberResponse(saved);
    }

    public MemberResponse updateMember(Integer id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member with ID " + id + " not found"));

        validateMemberRequest(request);

        String email = request.getEmail().trim().toLowerCase();
        if (memberRepository.existsByEmailAndMemberIdNot(email, id)) {
            throw new DuplicateResourceException("Member with email '" + email + "' already exists");
        }

        member.setFullName(request.getFullName().trim());
        member.setEmail(email);
        member.setContactNumber(request.getContactNumber().trim());
        member.setEmergencyContactName(request.getEmergencyContactName().trim());
        member.setEmergencyContactNumber(request.getEmergencyContactNumber().trim());

        Member updated = memberRepository.save(member);
        return new MemberResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getMemberRegistrations(Integer memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member with ID " + memberId + " not found");
        }
        List<Registration> list = registrationRepository.findByMember_MemberIdOrderByRegisteredAtDesc(memberId);
        return list.stream().map(RegistrationResponse::new).collect(Collectors.toList());
    }

    private void validateMemberRequest(MemberRequest request) {
        if (request == null) {
            throw new BadRequestException("Member data must not be empty");
        }
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new BadRequestException("Full name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() || !request.getEmail().contains("@")) {
            throw new BadRequestException("A valid email address is required");
        }
        if (request.getContactNumber() == null || request.getContactNumber().trim().length() < 7) {
            throw new BadRequestException("A valid contact number (at least 7 characters) is required");
        }
        if (request.getEmergencyContactName() == null || request.getEmergencyContactName().trim().isEmpty()) {
            throw new BadRequestException("Emergency contact name is required");
        }
        if (request.getEmergencyContactNumber() == null || request.getEmergencyContactNumber().trim().length() < 7) {
            throw new BadRequestException("A valid emergency contact number (at least 7 characters) is required");
        }
    }
}

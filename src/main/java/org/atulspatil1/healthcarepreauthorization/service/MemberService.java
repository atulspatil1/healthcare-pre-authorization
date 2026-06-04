package org.atulspatil1.healthcarepreauthorization.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.MemberRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.MemberResponseDto;
import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.atulspatil1.healthcarepreauthorization.exception.ResourceNotFoundException;
import org.atulspatil1.healthcarepreauthorization.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseDto registerMember(MemberRequestDto memberRequest) {

        String memberNumber = "MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        Member member = Member.builder()
                .memberNumber(memberNumber)
                .name(memberRequest.getName())
                .dob(memberRequest.getDob())
                .gender(memberRequest.getGender())
                .policyNumber(memberRequest.getPolicyNumber())
                .policyStatus(memberRequest.getPolicyStatus())
                .email(memberRequest.getEmail())
                .phone(memberRequest.getPhone())
                .createdAt(now)
                .updatedAt(now)
                .createdBy("SYSTEM")
                .updatedBy("SYSTEM")
                .build();

        Member savedMember = memberRepository.save(member);
        return mapToResponseDto(savedMember);
    }

    public MemberResponseDto getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
        return mapToResponseDto(member);
    }

    public MemberResponseDto getMemberByPolicyNumber(String policyNumber) {
        Member member = memberRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with policy number: " + policyNumber));
        return mapToResponseDto(member);
    }

    private MemberResponseDto mapToResponseDto(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .memberNumber(member.getMemberNumber())
                .name(member.getName())
                .dob(member.getDob())
                .gender(member.getGender())
                .policyNumber(member.getPolicyNumber())
                .policyStatus(member.getPolicyStatus())
                .email(member.getEmail())
                .phone(member.getPhone())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

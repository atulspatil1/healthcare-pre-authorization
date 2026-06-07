package org.atulspatil1.healthcarepreauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.healthcarepreauthorization.dto.request.MemberRequestDto;
import org.atulspatil1.healthcarepreauthorization.dto.response.MemberResponseDto;
import org.atulspatil1.healthcarepreauthorization.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<MemberResponseDto> registerMember(@RequestBody MemberRequestDto memberRequest) {
        MemberResponseDto response = memberService.registerMember(memberRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponseDto> getMember(@PathVariable Long id) {
        MemberResponseDto response = memberService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/members")
    public ResponseEntity<MemberResponseDto> lookupByPolicy(@RequestParam String policyNumber) {
        MemberResponseDto response = memberService.getMemberByPolicyNumber(policyNumber);
        return ResponseEntity.ok(response);
    }
}

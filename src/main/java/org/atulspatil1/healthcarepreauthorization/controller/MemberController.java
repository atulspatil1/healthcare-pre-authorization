package org.atulspatil1.healthcarepreauthorization.controller;

import org.atulspatil1.healthcarepreauthorization.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    @PostMapping("/members")
    public ResponseEntity<Member> registerMember(@RequestBody Member member) {
        return null;
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/members")
    public ResponseEntity<Member> lookupByPolicy(@RequestParam(required = false) String policyNumber) {
        return null;
    }
}

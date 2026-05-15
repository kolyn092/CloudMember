package com.cloudmember.controller;

import com.cloudmember.dto.CreateMemberRequest;
import com.cloudmember.dto.MemberResponse;
import com.cloudmember.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse response = memberService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getOneMember(@PathVariable Long memberId) {
        MemberResponse response = memberService.getOneMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

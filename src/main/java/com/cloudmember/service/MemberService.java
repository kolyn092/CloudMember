package com.cloudmember.service;

import com.cloudmember.dto.CreateMemberRequest;
import com.cloudmember.dto.MemberResponse;
import com.cloudmember.entity.Member;
import com.cloudmember.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse save(CreateMemberRequest memberRequest) {
        Member member = Member.to(memberRequest);
        memberRepository.save(member);
        return MemberResponse.from(member);
    }
}

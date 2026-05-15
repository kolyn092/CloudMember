package com.cloudmember.service;

import com.cloudmember.dto.CreateMemberRequest;
import com.cloudmember.dto.MemberResponse;
import com.cloudmember.entity.Member;
import com.cloudmember.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    private Member findMember(Long memeberId) {
        return memberRepository.findById(memeberId).orElseThrow(
                () -> new IllegalStateException("해당 멤버가 존재하지 않습니다.")
        );
    }

    @Transactional
    public MemberResponse save(CreateMemberRequest memberRequest) {
        Member member = Member.to(memberRequest);
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getOneMember(Long memberId) {
        Member member = findMember(memberId);

        return MemberResponse.from(member);
    }
}

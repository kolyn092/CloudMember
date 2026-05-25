package com.cloudmember.service;

import com.cloudmember.dto.CreateMemberRequest;
import com.cloudmember.dto.MemberResponse;
import com.cloudmember.entity.Member;
import com.cloudmember.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final IFileService fileService;

    public MemberService(MemberRepository memberRepository, IFileService fileService) {
        this.memberRepository = memberRepository;
        this.fileService = fileService;
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
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

    @Transactional
    public void uploadProfileImage(Long memberId, MultipartFile file) {
        Member member = findMember(memberId);

        String key = fileService.uploadProfileImage(memberId, file);

        member.updateProfileImageKey(key);
    }

    public String getProfileImageUrl(Long memberId) {
        Member member = findMember(memberId);

        return fileService.generateSignedUrl(member.getProfileImageKey());
    }
}

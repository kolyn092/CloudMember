package com.cloudmember.service;

import com.cloudmember.dto.MemberResponse;
import com.cloudmember.entity.Member;
import com.cloudmember.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("findMember 성공 - 존재하는 멤버 조회 시 멤버 정보를 반환한다")
    void findMember_Success() {
        // given
        Long memberId = 1L;
        Member member = new Member("홍길동", 25, "INTJ");

        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        // when
        MemberResponse response = memberService.getOneMember(memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.age()).isEqualTo(25);
        assertThat(response.mbti()).isEqualTo("INTJ");
    }
}

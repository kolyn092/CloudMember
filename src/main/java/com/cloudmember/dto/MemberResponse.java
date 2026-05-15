package com.cloudmember.dto;

import com.cloudmember.entity.Member;

public record MemberResponse(
        String name,
        Integer age,
        String mbti
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getName(),
                member.getAge(),
                member.getMbti()
        );
    }
}

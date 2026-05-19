package com.cloudmember.entity;

import com.cloudmember.dto.CreateMemberRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer age;

    private String mbti;

    private String profileImageKey;

    public Member(String name, Integer age, String mbti) {
        this.name = name;
        this.age = age;
        this.mbti = mbti;
        this.profileImageKey = null;
    }

    public static Member to(CreateMemberRequest request) {
        return new Member(
                request.name(),
                request.age(),
                request.mbti()
        );
    }

    public void updateProfileImageKey(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }
}

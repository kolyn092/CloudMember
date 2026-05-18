package com.cloudmember.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMemberRequest(

        @NotBlank
        String name,

        @NotNull
        Integer age,

        @NotBlank
        String mbti
) {
}

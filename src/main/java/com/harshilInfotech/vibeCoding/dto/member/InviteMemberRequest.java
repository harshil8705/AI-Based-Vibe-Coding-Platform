package com.harshilInfotech.vibeCoding.dto.member;

import com.harshilInfotech.vibeCoding.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @Email(message = "Please Entier the valid Email")
        @NotBlank
        String username,

        @NotNull(message = "The role cannot be empty")
        ProjectRole role
) {
}
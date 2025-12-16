package com.harshilInfotech.vibeCoding.dto.member;

import com.harshilInfotech.vibeCoding.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(

        @NotNull(message = "The role cannot be empty")
        ProjectRole role
) {
}
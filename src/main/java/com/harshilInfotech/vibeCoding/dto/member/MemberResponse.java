package com.harshilInfotech.vibeCoding.dto.member;

import com.harshilInfotech.vibeCoding.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        ProjectRole projectRole,
        Instant invitedAt
) {
}
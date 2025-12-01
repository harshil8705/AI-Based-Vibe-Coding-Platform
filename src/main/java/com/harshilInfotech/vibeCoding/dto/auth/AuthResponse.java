package com.harshilInfotech.vibeCoding.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}

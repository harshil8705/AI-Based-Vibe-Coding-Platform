package com.harshilInfotech.vibeCoding.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(
        @NotBlank
        String name
) {
}
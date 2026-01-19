package com.harshilInfotech.vibeCoding.dto.chat;

public record ChatRequest(
        String message,
        Long projectId
) {
}

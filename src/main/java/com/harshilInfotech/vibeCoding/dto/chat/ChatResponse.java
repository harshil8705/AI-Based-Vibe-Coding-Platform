package com.harshilInfotech.vibeCoding.dto.chat;

import com.harshilInfotech.vibeCoding.entity.ChatEvent;
import com.harshilInfotech.vibeCoding.entity.ChatSession;
import com.harshilInfotech.vibeCoding.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokensUsed,
        Instant createdAt

) {
}

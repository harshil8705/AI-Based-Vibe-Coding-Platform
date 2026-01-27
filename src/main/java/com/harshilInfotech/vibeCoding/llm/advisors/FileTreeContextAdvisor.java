package com.harshilInfotech.vibeCoding.llm.advisors;

import com.harshilInfotech.vibeCoding.dto.project.FileNode;
import com.harshilInfotech.vibeCoding.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Messages;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain streamAdvisorChain) {

        Map<String, Object> context = request.context();
        Long projectId = Long.parseLong(context.getOrDefault("projectId", 0).toString());

        ChatClientRequest augmentedChatClientRequest = augmentRequestWithFileTree(request, projectId);

        return streamAdvisorChain.nextStream(augmentedChatClientRequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request, Long projectId) {

        List<Message> incompleteMessages = request.prompt().getInstructions();

        Message systemMessage = incompleteMessages.stream()
                .filter(m -> m.getMessageType() == MessageType.SYSTEM)
                .findFirst()
                .orElse(null);

        List<Message> userMessages = incompleteMessages.stream()
                .filter(m -> m.getMessageType() != MessageType.SYSTEM)
                .toList();

        List<Message> allMessages = new ArrayList<>();

//        if (systemMessage != null) {
//            allMessages.add(systemMessage);
//        }

        List<FileNode> fileTree = projectFileService.getFileTree(projectId);
        String fileTreeContext = "\n\n ----- FILE_TREE ----- \n" + fileTree.toString();
//        allMessages.add(new SystemMessage(fileTreeContext));

        if (systemMessage != null) {

            String combinedContent = systemMessage.getText() + fileTreeContext;
            allMessages.add(new SystemMessage(combinedContent));

        } else {

            allMessages.add(new SystemMessage(fileTreeContext));

        }

        allMessages.addAll(userMessages);

        return request
                .mutate()
                .prompt(new Prompt(allMessages, request.prompt().getOptions()))
                .build();

    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

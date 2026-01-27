package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.llm.PromptUtils;
import com.harshilInfotech.vibeCoding.llm.advisors.FileTreeContextAdvisor;
import com.harshilInfotech.vibeCoding.llm.tools.CodeGenerationTools;
import com.harshilInfotech.vibeCoding.security.AuthUtil;
import com.harshilInfotech.vibeCoding.service.AiGenerationService;
import com.harshilInfotech.vibeCoding.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        log.info("streamResponse called - projectId: {}", projectId);

        Long userId = authUtil.getCurrentUserId();
        log.info("Current userId: {}", userId);


        // Check permission manually BEFORE starting the stream
//        ProjectMember member = projectMemberRepository
//                .findById(new ProjectMemberId(projectId, userId))
//                .orElseThrow(() -> new AccessDeniedException("No access to project"));
//
//        if (!member.getProjectRole().getPermissions().contains(ProjectPermission.EDIT)) {
//            return Flux.error(new AccessDeniedException("Insufficient permissions"));
//        }

        createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        return chatClient
                .prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParams);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(response -> {
                    String content = response.getResult().getOutput().getText();
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(() -> {
//                    Schedulers.boundedElastic().schedule(() -> {
//                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
//                    });

                    Mono.fromRunnable(() -> parseAndSaveFiles(fullResponseBuffer.toString(), projectId))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnError(error -> log.error("Failed to save files for project {}", projectId, error))
                            .subscribe();

                })
                .doOnError(error -> log.error("Error during streaming for projectId: {}", projectId))
                .map(response -> Objects.requireNonNull(response.getResult().getOutput().getText()));

    }

    private void parseAndSaveFiles(String fullResponse, Long projectId) {

//        <--  FOR REFERENCE -->
//        String dummy = """
//                <message> I'm going to read the files and generate the code </message>
//                <file path="src/App.jsx">
//                    import App from './App.jsx'
//                    .....
//                </file>
//                <message> I'm going to read the files and generate the code </message>
//                <file path="src/App.jsx">
//                    import App from './App.jsx'
//                    .....
//                </file>
//                """;

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        while (matcher.find()) {

            String filePath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            projectFileService.saveFile(projectId, filePath, fileContent);

        }

    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {



    }

}

package com.harshilInfotech.vibeCoding.security;

import com.harshilInfotech.vibeCoding.enums.ProjectPermission;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.harshilInfotech.vibeCoding.enums.ProjectPermission.*;

@Component("security")
@RequiredArgsConstructor
@Slf4j
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    private Boolean hasPermissions(Long projectId, ProjectPermission projectPermission) {
        Long userId = authUtil.getCurrentUserId();

        log.info("Checking permission - userId: {}, projectId: {}, permission: {}",
                userId, projectId, projectPermission);

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> {
                            log.info("Found role: {}, permissions: {}", role, role.getPermissions());
                            boolean hasPermission = role.getPermissions().contains(projectPermission);
                            log.info("Has {} permission: {}", projectPermission, hasPermission);
                            return hasPermission;
                        })
                .orElseGet(() -> {
                    log.warn("No role found for userId: {} and projectId: {}", userId, projectId);
                    return false;
                });
    }

    public Boolean canViewProject(Long projectId) {
        return hasPermissions(projectId, VIEW);
    }

    public Boolean canEditProject(Long projectId) {
        return hasPermissions(projectId, EDIT);
    }

    public Boolean canDeleteProject(Long projectId) {
        return hasPermissions(projectId, DELETE);
    }

    public Boolean canViewMembers(Long projectId) {
        return hasPermissions(projectId, VIEW_MEMBERS);
    }

    public Boolean canManageMembers(Long projectId) {
        return hasPermissions(projectId, MANAGE_MEMBERS);
    }
}
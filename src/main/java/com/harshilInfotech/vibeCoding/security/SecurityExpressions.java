package com.harshilInfotech.vibeCoding.security;

import com.harshilInfotech.vibeCoding.enums.ProjectPermission;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.harshilInfotech.vibeCoding.enums.ProjectPermission.*;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;

    private Boolean hasPermissions(Long projectId, ProjectPermission projectPermission) {
        Long userId = authUtil.getCurrentUserId();

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> role.getPermissions().contains(projectPermission))
                .orElse(false);
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
package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.dto.project.ProjectRequest;
import com.harshilInfotech.vibeCoding.dto.project.ProjectResponse;
import com.harshilInfotech.vibeCoding.dto.project.ProjectSummaryResponse;
import com.harshilInfotech.vibeCoding.entity.Project;
import com.harshilInfotech.vibeCoding.entity.ProjectMember;
import com.harshilInfotech.vibeCoding.entity.ProjectMemberId;
import com.harshilInfotech.vibeCoding.entity.User;
import com.harshilInfotech.vibeCoding.enums.ProjectRole;
import com.harshilInfotech.vibeCoding.error.BadRequestException;
import com.harshilInfotech.vibeCoding.error.ResourceNotFoundException;
import com.harshilInfotech.vibeCoding.mapper.ProjectMapper;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import com.harshilInfotech.vibeCoding.repository.ProjectRepository;
import com.harshilInfotech.vibeCoding.repository.UserRepository;
import com.harshilInfotech.vibeCoding.security.AuthUtil;
import com.harshilInfotech.vibeCoding.service.ProjectService;
import com.harshilInfotech.vibeCoding.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {

        if (!subscriptionService.canCreateProject()) {
            throw new BadRequestException("User cannot create a New Project with current Plan, Upgrad plan now.");
        }

        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        User owner = userRepository.getReferenceById(userId);

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projects = projectRepository.findAllAccessibleByUser(userId);

        return projectMapper.toListOfProjectSummaryResponse(projects);
        
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProject(projectId, userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {

        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProject(projectId, userId);

        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        Project project = getAccessibleProject(projectId, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }

    public Project getAccessibleProject(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
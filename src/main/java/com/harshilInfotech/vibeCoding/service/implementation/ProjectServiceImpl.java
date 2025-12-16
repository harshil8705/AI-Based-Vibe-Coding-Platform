package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.dto.project.ProjectRequest;
import com.harshilInfotech.vibeCoding.dto.project.ProjectResponse;
import com.harshilInfotech.vibeCoding.dto.project.ProjectSummaryResponse;
import com.harshilInfotech.vibeCoding.entity.Project;
import com.harshilInfotech.vibeCoding.entity.ProjectMember;
import com.harshilInfotech.vibeCoding.entity.ProjectMemberId;
import com.harshilInfotech.vibeCoding.entity.User;
import com.harshilInfotech.vibeCoding.enums.ProjectRole;
import com.harshilInfotech.vibeCoding.error.ResourceNotFoundException;
import com.harshilInfotech.vibeCoding.mapper.ProjectMapper;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import com.harshilInfotech.vibeCoding.repository.ProjectRepository;
import com.harshilInfotech.vibeCoding.repository.UserRepository;
import com.harshilInfotech.vibeCoding.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest request, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

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
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
//        return projectRepository.findAllAccessibleByUser(userId)
//                .stream()
//                .map(projectMapper::toProjectSummaryResponse)
//                .collect(Collectors.toList());

        var projects = projectRepository.findAllAccessibleByUser(userId);

        return projectMapper.toListOfProjectSummaryResponse(projects);
        
    }

    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
        Project project = getAccessibleProject(id, userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, ProjectRequest request, Long userId) {

        Project project = getAccessibleProject(id, userId);

        project.setName(request.name());
        project = projectRepository.save(project);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {

        Project project = getAccessibleProject(id, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

    }

    public Project getAccessibleProject(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
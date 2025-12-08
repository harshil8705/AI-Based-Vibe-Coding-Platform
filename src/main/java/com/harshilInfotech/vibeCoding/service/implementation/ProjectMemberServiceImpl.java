package com.harshilInfotech.vibeCoding.service.implementation;

import com.harshilInfotech.vibeCoding.dto.member.InviteMemberRequest;
import com.harshilInfotech.vibeCoding.dto.member.MemberResponse;
import com.harshilInfotech.vibeCoding.dto.member.UpdateMemberRoleRequest;
import com.harshilInfotech.vibeCoding.entity.Project;
import com.harshilInfotech.vibeCoding.entity.ProjectMember;
import com.harshilInfotech.vibeCoding.entity.ProjectMemberId;
import com.harshilInfotech.vibeCoding.entity.User;
import com.harshilInfotech.vibeCoding.mapper.ProjectMemberMapper;
import com.harshilInfotech.vibeCoding.repository.ProjectMemberRepository;
import com.harshilInfotech.vibeCoding.repository.ProjectRepository;
import com.harshilInfotech.vibeCoding.repository.UserRepository;
import com.harshilInfotech.vibeCoding.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMember(Long projectId, Long userId) {

        Project project = getAccessibleProject(projectId, userId);

        List<MemberResponse> memberResponses = new ArrayList<>();
        memberResponses.add(projectMemberMapper.toMemberResponseFromOwner(project.getOwner()));

        memberResponses.addAll(
            projectMemberRepository.findByIdProjectId(projectId)
                    .stream()
                    .map(projectMemberMapper::toMemberResponseFromMember)
                    .toList()
        );

        return memberResponses;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {

        Project project = getAccessibleProject(projectId, userId);

        if (!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not Allowed!! Only the owner of this project can send invitation.");
        }

        User invitee = userRepository.findByEmail(request.email()).orElseThrow();

        if (invitee.getId().equals(userId)) {
            throw new RuntimeException("Cannot Invite Yourself!!");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Invitee already present in this Project. Cannot invite once again!!");
        }

        ProjectMember member = ProjectMember.builder()
                .id(projectMemberId)
                .invitedAt(Instant.now())
                .project(project)
                .user(invitee)
                .projectRole(request.role())
                .build();

        projectMemberRepository.save(member);

        return projectMemberMapper.toMemberResponseFromMember(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId) {

        Project project = getAccessibleProject(projectId, userId);

        if (!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not Allowed!! Only the owner of this project can send invitation.");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId).orElseThrow();

        projectMember.setProjectRole(request.role());

        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toMemberResponseFromMember(projectMember);
    }

    @Override
    public MemberResponse removeProjectMember(Long memberId, Long projectId, Long userId) {
        Project project = getAccessibleProject(projectId, userId);
        if (!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Not Allowed!!");
        }

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if (!projectMemberRepository.existsById(projectMemberId)) {
            throw new RuntimeException("Member doesn't exist in Project!!");
        }

        projectMemberRepository.deleteById(projectMemberId);

        return null;
    }

    public Project getAccessibleProject(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow();
    }
}

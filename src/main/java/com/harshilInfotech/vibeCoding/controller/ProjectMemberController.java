package com.harshilInfotech.vibeCoding.controller;

import com.harshilInfotech.vibeCoding.dto.member.InviteMemberRequest;
import com.harshilInfotech.vibeCoding.dto.member.MemberResponse;
import com.harshilInfotech.vibeCoding.dto.member.UpdateMemberRoleRequest;
import com.harshilInfotech.vibeCoding.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.getProjectMember(projectId, userId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InviteMemberRequest request
    ) {
        Long userId = 1L;
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(projectMemberService.inviteMember(projectId, request, userId));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRoleRequest request
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request, userId));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long memberId,
            @PathVariable Long projectId
    ) {
        Long userId = 1L;

        projectMemberService.removeProjectMember(memberId, projectId, userId);
        return ResponseEntity.noContent().build();
    }

}
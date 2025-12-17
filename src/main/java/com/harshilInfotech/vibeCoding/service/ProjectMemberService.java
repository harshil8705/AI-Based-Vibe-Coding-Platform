package com.harshilInfotech.vibeCoding.service;

import com.harshilInfotech.vibeCoding.dto.member.InviteMemberRequest;
import com.harshilInfotech.vibeCoding.dto.member.MemberResponse;
import com.harshilInfotech.vibeCoding.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {

    List<MemberResponse> getProjectMember(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    MemberResponse removeProjectMember(Long memberId, Long projectId);

}
package com.harshilInfotech.vibeCoding.mapper;

import com.harshilInfotech.vibeCoding.dto.member.MemberResponse;
import com.harshilInfotech.vibeCoding.entity.ProjectMember;
import com.harshilInfotech.vibeCoding.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "projectRole", constant = "OWNER")
    MemberResponse toMemberResponseFromOwner(User owner);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "name", source = "user.name")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);

}
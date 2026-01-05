package com.harshilInfotech.vibeCoding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.harshilInfotech.vibeCoding.enums.ProjectPermission.*;

@Getter
@RequiredArgsConstructor
public enum ProjectRole {
    EDITOR(Set.of(VIEW, EDIT, DELETE, VIEW_MEMBERS)),
    VIEWER(Set.of(VIEW, VIEW_MEMBERS)),
    OWNER(Set.of(VIEW, EDIT, DELETE, MANAGE_MEMBERS, VIEW_MEMBERS));

    private final Set<ProjectPermission> permissions;
}

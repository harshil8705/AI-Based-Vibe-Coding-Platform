package com.harshilInfotech.vibeCoding.dto.project;

public record FileNode(
        String path
) {

    @Override
    public String toString() {
        return path;
    }

}

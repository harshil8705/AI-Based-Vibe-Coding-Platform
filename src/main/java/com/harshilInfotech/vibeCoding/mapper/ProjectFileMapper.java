package com.harshilInfotech.vibeCoding.mapper;

import com.harshilInfotech.vibeCoding.dto.project.FileNode;
import com.harshilInfotech.vibeCoding.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);

}

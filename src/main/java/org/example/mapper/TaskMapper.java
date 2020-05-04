package org.example.mapper;

import org.example.dto.TaskDto;
import org.example.model.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class TaskMapper extends AbstractMapper<TaskDto, TaskEntity> {
}

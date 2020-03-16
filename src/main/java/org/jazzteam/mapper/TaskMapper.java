package org.jazzteam.mapper;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.model.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDto toDto(TaskEntity taskEntity);

    TaskEntity toEntity(TaskDto taskDto);
}

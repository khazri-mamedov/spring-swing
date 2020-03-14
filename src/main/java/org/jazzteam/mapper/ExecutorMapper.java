package org.jazzteam.mapper;

import org.jazzteam.dto.ExecutorDto;
import org.jazzteam.model.ExecutorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExecutorMapper {
    ExecutorDto toDto(ExecutorEntity executorEntity);

    ExecutorEntity toEntity(ExecutorDto executorDto);
}

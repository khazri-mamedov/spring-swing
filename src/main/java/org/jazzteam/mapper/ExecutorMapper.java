package org.jazzteam.mapper;

import org.jazzteam.dto.PerformerDto;
import org.jazzteam.model.PerformerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExecutorMapper {
    PerformerDto toDto(PerformerEntity performerEntity);

    PerformerEntity toEntity(PerformerDto performerDto);
}

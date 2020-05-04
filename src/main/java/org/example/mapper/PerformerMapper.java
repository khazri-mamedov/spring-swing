package org.example.mapper;

import org.example.dto.PerformerDto;
import org.example.model.PerformerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PerformerMapper extends AbstractMapper<PerformerDto, PerformerEntity> {
}

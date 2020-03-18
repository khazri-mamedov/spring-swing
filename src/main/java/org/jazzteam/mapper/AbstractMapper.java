package org.jazzteam.mapper;

public abstract class AbstractMapper<D, E> {
    public abstract D toDto(E entity);
    public abstract E toEntity(D dto);
}

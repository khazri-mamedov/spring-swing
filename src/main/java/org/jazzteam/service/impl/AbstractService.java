package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.AbstractDto;
import org.jazzteam.mapper.AbstractMapper;
import org.jazzteam.model.AbstractEntity;
import org.jazzteam.repository.CrudRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractService<U, D extends AbstractDto<U>, A,
        E extends AbstractEntity<U>, M extends AbstractMapper<D, E>, R extends CrudRepository<E, U>> {
    protected final R repository;
    protected final M mapper;
    protected final ExecutorService executorService;
    protected final AmqpTemplate amqpTemplate;
    protected final ApplicationEventPublisher applicationEventPublisher;

    public void deleteById(U id) {
        repository.deleteById(id);
    }

    public void deleteSelected(D dto) {
        executorService.execute(() -> {
            deleteById(dto.getId());
            A taskAction = createDeleteAction(dto.getId());
            produceMessage(taskAction);
        });
    }

    public void create(D dto) {
        executorService.execute(() -> {
            E savedEntity = repository.save(mapper.toEntity(dto));
            D savedDto = mapper.toDto(repository.findByIdOrThrow(savedEntity.getId()));
            A action = createCreateAction(savedDto);
            produceMessage(action);
        });
    }

    public void update(D dto) {
        executorService.execute(() -> {
            E updatedEntity = repository.save(mapper.toEntity(dto));
            D updatedDto = mapper.toDto(updatedEntity);
            A action = createEditAction(updatedDto);
            produceMessage(action);
        });
    }

    public List<D> getAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    protected abstract A createDeleteAction(U id);

    protected abstract A createCreateAction(D dto);

    protected abstract A createEditAction(D dto);

    protected void produceMessage(A action) {
        amqpTemplate.convertAndSend(getExchangeName(), "", action);
    }

    protected abstract String getExchangeName();
}

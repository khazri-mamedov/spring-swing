package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.gui.action.performer.CreateAction;
import org.jazzteam.gui.action.performer.DeleteAction;
import org.jazzteam.gui.action.performer.EditAction;
import org.jazzteam.gui.action.performer.PerformerAction;
import org.jazzteam.mapper.PerformerMapper;
import org.jazzteam.model.PerformerEntity;
import org.jazzteam.repository.PerformerRepository;
import org.jazzteam.repository.TaskRepository;
import org.jazzteam.service.PerformerService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformerServiceImpl implements PerformerService {
    @Value("${message.broker.performer.exchange.name}")
    private String exchangeName;

    private final PerformerRepository performerRepository;
    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PerformerMapper performerMapper;
    private final ExecutorService executorService;
    private final AmqpTemplate amqpTemplate;

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param performerAction execute logic
     */
    public void handleMessage(PerformerAction performerAction) {
        performerAction.execute(applicationEventPublisher);
    }

    @Override
    public List<PerformerDto> getAllPerformers() {
        return performerRepository.findAll().stream().map(performerMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteSelectedPerformer(PerformerDto performerDto) {
        executorService.execute(() -> {
            deleteById(performerDto.getId());
            PerformerAction performerAction = new DeleteAction(performerDto.getId());
            produceMessage(performerAction);
        });
    }

    @Override
    public void deleteById(int id) {
        performerRepository.deleteById(id);
    }

    @Override
    public boolean isDeletable(int id) {
        // Custom query instead ManyToOne (performance issue)
        return !taskRepository.existsPerformer(id);
    }

    @Override
    public void createPerformer(PerformerDto performerDto) {
        executorService.execute(() -> {
            PerformerEntity savedPerformerEntity = performerRepository.save(performerMapper.toEntity(performerDto));
            PerformerDto savedPerformerDto = performerMapper.toDto(performerRepository.findByIdOrThrow(savedPerformerEntity.getId()));
            PerformerAction performerAction = new CreateAction(savedPerformerDto);
            produceMessage(performerAction);
        });
    }

    @Override
    public void updatePerformer(PerformerDto performerDto) {
        executorService.execute(() -> {
            PerformerEntity updatedPerformerEntity = performerRepository.save(performerMapper.toEntity(performerDto));
            PerformerDto updatedPerformerDto = performerMapper.toDto(updatedPerformerEntity);
            PerformerAction performerAction = new EditAction(updatedPerformerDto);
            produceMessage(performerAction);
        });
    }

    private void produceMessage(PerformerAction performerAction) {
        amqpTemplate.convertAndSend(exchangeName, "", performerAction);
    }
}

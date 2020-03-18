package org.jazzteam.service.impl;

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

import java.util.concurrent.ExecutorService;

@Service
public class PerformerServiceImpl
        extends AbstractService<Integer, PerformerDto, PerformerAction,
        PerformerEntity, PerformerMapper, PerformerRepository>
        implements PerformerService {
    @Value("${message.broker.performer.exchange.name}")
    private String exchangeName;

    private final TaskRepository taskRepository;

    public PerformerServiceImpl(
            PerformerRepository repository,
            PerformerMapper mapper,
            ExecutorService executorService,
            AmqpTemplate amqpTemplate,
            ApplicationEventPublisher applicationEventPublisher,
            TaskRepository taskRepository) {
        super(repository, mapper, executorService, amqpTemplate, applicationEventPublisher);
        this.taskRepository = taskRepository;
    }

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param performerAction execute logic
     */
    public void handleMessage(PerformerAction performerAction) {
        performerAction.execute(applicationEventPublisher);
    }

    @Override
    public boolean isDeletable(int id) {
        // Custom query instead ManyToOne (performance issue)
        return !taskRepository.existsPerformer(id);
    }

    @Override
    protected PerformerAction createDeleteAction(Integer id) {
        return new DeleteAction(id);
    }

    @Override
    protected PerformerAction createCreateAction(PerformerDto dto) {
        return new CreateAction(dto);
    }

    @Override
    protected PerformerAction createEditAction(PerformerDto dto) {
        return new EditAction(dto);
    }

    @Override
    protected String getExchangeName() {
        return exchangeName;
    }
}

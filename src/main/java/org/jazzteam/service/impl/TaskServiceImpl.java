package org.jazzteam.service.impl;

import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.task.CreateAction;
import org.jazzteam.gui.action.task.DeleteAction;
import org.jazzteam.gui.action.task.EditAction;
import org.jazzteam.gui.action.task.MoveAction;
import org.jazzteam.gui.action.task.SwapAction;
import org.jazzteam.gui.action.task.TaskAction;
import org.jazzteam.gui.event.task.MoveEventType;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.jazzteam.service.TaskService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl
        extends AbstractService<Integer, TaskDto, TaskAction, TaskEntity, TaskMapper, TaskRepository>
        implements TaskService {
    @Value("${message.broker.task.exchange.name}")
    private String exchangeName;

    public TaskServiceImpl(
            TaskRepository repository,
            TaskMapper mapper,
            ExecutorService executorService,
            AmqpTemplate amqpTemplate,
            ApplicationEventPublisher applicationEventPublisher) {
        super(repository, mapper, executorService, amqpTemplate, applicationEventPublisher);
    }

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param taskAction execute logic
     */
    public void handleMessage(TaskAction taskAction) {
        taskAction.execute(applicationEventPublisher);
    }

    @Override
    public List<TaskDto> getAllTasksOrdered() {
        return repository.findAllByOrderByOrderId()
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void moveTask(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto, MoveEventType moveEventType) {
        executorService.execute(() -> {
            moveTasks(firstSelectedTaskDto, secondSelectedTaskDto);
            final TaskAction taskAction = new MoveAction(
                    firstSelectedTaskDto.getId(),
                    secondSelectedTaskDto.getId(),
                    moveEventType
            );
            produceMessage(taskAction);
        });
    }

    @Override
    public void swapTasks(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto) {
        executorService.execute(() -> {
            moveTasks(firstSelectedTaskDto, secondSelectedTaskDto);
            final TaskAction taskAction = new SwapAction(
                    firstSelectedTaskDto.getId(),
                    secondSelectedTaskDto.getId()
            );
            produceMessage(taskAction);
        });
    }

    private void moveTasks(TaskDto firstSelectedTaskDto, TaskDto secondSelectedTaskDto) {
        TaskEntity secondTaskEntity = mapper.toEntity(secondSelectedTaskDto);
        TaskEntity firstTaskEntity = mapper.toEntity(firstSelectedTaskDto);

        secondTaskEntity.setOrderId(firstSelectedTaskDto.getOrderId());
        firstTaskEntity.setOrderId(secondSelectedTaskDto.getOrderId());

        repository.updateOrders(firstTaskEntity, secondTaskEntity);
    }

    @Override
    protected TaskAction createDeleteAction(Integer id) {
        return new DeleteAction(id);
    }

    @Override
    protected TaskAction createCreateAction(TaskDto dto) {
        return new CreateAction(dto);
    }

    @Override
    protected TaskAction createEditAction(TaskDto dto) {
        return new EditAction(dto);
    }

    @Override
    protected String getExchangeName() {
        return exchangeName;
    }
}

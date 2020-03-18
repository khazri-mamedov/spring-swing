package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    @Value("${message.broker.task.exchange.name}")
    private String exchangeName;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;
    private final AmqpTemplate amqpTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param taskAction execute logic
     */
    public void handleMessage(TaskAction taskAction) {
        taskAction.execute(applicationEventPublisher);
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAllByOrderByOrderId()
                .stream().map(taskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteSelectedTask(TaskDto taskDto) {
        executorService.execute(() -> {
            deleteById(taskDto.getId());
            TaskAction taskAction = new DeleteAction(taskDto.getId());
            produceMessage(taskAction);
        });
    }

    @Override
    public void deleteById(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public void updateTask(TaskDto taskDto) {
        executorService.execute(() -> {
            TaskEntity updatedTaskEntity = taskRepository.save(taskMapper.toEntity(taskDto));
            TaskDto updatedTaskDto = taskMapper.toDto(updatedTaskEntity);
            TaskAction taskAction = new EditAction(updatedTaskDto);
            produceMessage(taskAction);
        });
    }

    @Override
    public void createTask(TaskDto taskDto) {
        executorService.execute(() -> {
            TaskEntity savedTaskEntity = taskRepository.save(taskMapper.toEntity(taskDto));
            TaskDto savedTaskDto = taskMapper.toDto(taskRepository.findByIdOrThrow(savedTaskEntity.getId()));
            TaskAction taskAction = new CreateAction(savedTaskDto);
            produceMessage(taskAction);
        });
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
        TaskEntity secondTaskEntity = taskMapper.toEntity(secondSelectedTaskDto);
        TaskEntity firstTaskEntity = taskMapper.toEntity(firstSelectedTaskDto);

        secondTaskEntity.setOrderId(firstSelectedTaskDto.getOrderId());
        firstTaskEntity.setOrderId(secondSelectedTaskDto.getOrderId());

        taskRepository.updateOrders(firstTaskEntity, secondTaskEntity);
    }

    private void produceMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }
}

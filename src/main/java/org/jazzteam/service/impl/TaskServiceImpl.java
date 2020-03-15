package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.*;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.jazzteam.service.TaskService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final ExecutorService executorService;
    private final AmqpTemplate amqpTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    private TaskTableModel taskTableModel;

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param taskAction execute logic
     */
    public void handleMessage(TaskAction taskAction) {
        taskAction.execute(taskTableModel, this, applicationEventPublisher);
    }

    @Override
    public TaskTableModel createAndPopulateTaskTableModel() {
        taskTableModel = new TaskTableModel();
        List<TaskDto> tasks = getAllTasks();
        tasks.forEach(taskDto -> EventQueue.invokeLater(() -> taskTableModel.addRow(taskDto)));
        return taskTableModel;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAllByOrderByOrderId()
                .stream().map(taskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteSelectedTask(int rowIndex) {
        executorService.execute(() -> {
            TaskDto selectedTaskDto = taskTableModel.getTasks().get(rowIndex);
            deleteById(selectedTaskDto.getId());
            TaskAction taskAction = new DeleteAction(rowIndex, selectedTaskDto.getId());
            produceMessage(taskAction);
        });
    }

    @Override
    public void deleteById(int id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto getSelectedTask(int rowIndex) {
        return taskTableModel.getTasks().get(rowIndex);
    }

    @Override
    public void updateTask(TaskDto taskDto, int rowIndex) {
        executorService.execute(() -> {
            TaskEntity updatedTaskEntity = taskRepository.save(taskMapper.toEntity(taskDto));
            TaskDto updatedTaskDto = taskMapper.toDto(updatedTaskEntity);
            TaskAction taskAction = new EditAction(updatedTaskDto, rowIndex);
            produceMessage(taskAction);
        });
    }

    @Override
    public void createTask(TaskDto taskDto) {
        executorService.execute(() -> {
            TaskEntity savedTaskEntity = taskRepository.save(taskMapper.toEntity(taskDto));
            TaskAction taskAction = new CreateAction(savedTaskEntity.getId());
            produceMessage(taskAction);
        });
    }

    @Override
    public TaskTableModel createAndRepopulateModel() {
        // Mark eligible for GC
        taskTableModel.getTasks().clear();
        return createAndPopulateTaskTableModel();
    }

    @Override
    public void moveUpTask(int selectedRowIndex, TaskDto selectedTaskDto) {
        int upRowIndex = selectedRowIndex - 1;
        //int nextRowIndex = selectedRowIndex + 1;
        final TaskDto prevTaskDto = getSelectedTask(upRowIndex);
        //final TaskDto nextTaskDto = taskTableModel.getTasks().get(nextRowIndex);
        swapTasks(selectedRowIndex, upRowIndex, prevTaskDto, selectedTaskDto);
        executorService.execute(() -> {
            TaskEntity prevTaskEntity = taskMapper.toEntity(prevTaskDto);
            TaskEntity selectedTaskEntity = taskMapper.toEntity(selectedTaskDto);
            taskRepository.updateOrders(prevTaskEntity, selectedTaskEntity);
            TaskAction taskAction
                    = new MoveAction(selectedRowIndex, selectedTaskEntity.getOrderId(), prevTaskEntity.getOrderId());
            produceMessage(taskAction);
        });
    }

    private void swapTasks(int selectedRowIndex, int upRowIndex, TaskDto prevTaskDto, TaskDto selectedTaskDto) {
        Collections.swap(taskTableModel.getTasks(), upRowIndex, selectedRowIndex);
        int swapOrderId = prevTaskDto.getOrderId();
        prevTaskDto.setOrderId(selectedTaskDto.getOrderId());
        selectedTaskDto.setOrderId(swapOrderId);
    }

    private void produceMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }
}
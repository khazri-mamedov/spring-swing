package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.TaskDto;
import org.jazzteam.gui.action.CreateAction;
import org.jazzteam.gui.action.DeleteAction;
import org.jazzteam.gui.action.EditAction;
import org.jazzteam.gui.action.MoveAction;
import org.jazzteam.gui.action.SwapAction;
import org.jazzteam.gui.action.TaskAction;
import org.jazzteam.gui.event.MoveEventType;
import org.jazzteam.gui.table.TaskTableModel;
import org.jazzteam.mapper.TaskMapper;
import org.jazzteam.model.TaskEntity;
import org.jazzteam.repository.TaskRepository;
import org.jazzteam.service.TaskService;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
            TaskDto savedTaskDto = taskMapper.toDto(taskRepository.findByIdOrThrow(savedTaskEntity.getId()));
            TaskAction taskAction = new CreateAction(savedTaskDto);
            produceMessage(taskAction);
        });
    }

    @Override
    public void addTask(TaskDto savedTaskDto) {
        taskTableModel.insertRow(savedTaskDto);
    }

    @Override
    public void moveTask(int selectedRowIndex, int rowIndex, MoveEventType moveEventType) {
        executorService.execute(() -> {
            final TaskDto selectedTaskDto = getSelectedTask(selectedRowIndex);
            final TaskDto prevTaskDto = getSelectedTask(rowIndex);
            swapTasks(prevTaskDto, selectedTaskDto);
            TaskEntity prevTaskEntity = taskMapper.toEntity(prevTaskDto);
            TaskEntity selectedTaskEntity = taskMapper.toEntity(selectedTaskDto);
            taskRepository.updateOrders(prevTaskEntity, selectedTaskEntity);
            final TaskAction taskAction = new MoveAction(
                    selectedRowIndex,
                    rowIndex,
                    selectedTaskEntity.getOrderId(),
                    prevTaskEntity.getOrderId(),
                    moveEventType
            );
            produceMessage(taskAction);
        });
    }

    @Override
    public int getTaskCount() {
        return taskTableModel.getTasks().size() - 1;
    }

    @Override
    public LinkedList<TaskDto> getSelectedTasks(Set<Integer> selectedRows) {
        return selectedRows.stream().map(this::getSelectedTask).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void swapTasks(int firstSelectedRow, int secondSelectedRow) {
        executorService.execute(() -> {
            final LinkedList<TaskDto> selectedTaskDtos
                    = getSelectedTasks(Collections.asSet(firstSelectedRow, secondSelectedRow));
            if (selectedTaskDtos.size() > 1) {
                TaskDto firstSelectedTask = selectedTaskDtos.getFirst();
                TaskDto secondSelectedTask = selectedTaskDtos.getLast();
                swapTasks(firstSelectedTask, secondSelectedTask);
                taskRepository
                        .updateOrders(taskMapper.toEntity(firstSelectedTask), taskMapper.toEntity(secondSelectedTask));
                final TaskAction taskAction = new SwapAction(
                        firstSelectedTask,
                        secondSelectedTask,
                        firstSelectedRow,
                        secondSelectedRow
                );
                produceMessage(taskAction);
            }
        });
    }

    private void swapTasks(TaskDto prevTaskDto, TaskDto selectedTaskDto) {
        int swapOrderId = prevTaskDto.getOrderId();
        prevTaskDto.setOrderId(selectedTaskDto.getOrderId());
        selectedTaskDto.setOrderId(swapOrderId);
    }

    private void produceMessage(TaskAction taskAction) {
        amqpTemplate.convertAndSend(exchangeName, "", taskAction);
    }
}

package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.dto.PerformerDto;
import org.jazzteam.gui.action.executor.ExecutorAction;
import org.jazzteam.mapper.PerformerMapper;
import org.jazzteam.repository.PerformerRepository;
import org.jazzteam.service.PerformerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformerServiceImpl implements PerformerService {
    @Value("${message.broker.performer.exchange.name}")
    private String exchangeName;

    private final PerformerRepository performerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PerformerMapper performerMapper;

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param executorAction execute logic
     */
    public void handleMessage(ExecutorAction executorAction) {

    }

    @Override
    public List<PerformerDto> getAllPerformers() {
        return performerRepository.findAll().stream().map(performerMapper::toDto).collect(Collectors.toList());
    }
}

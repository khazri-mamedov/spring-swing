package org.jazzteam.service.impl;

import lombok.RequiredArgsConstructor;
import org.jazzteam.gui.action.executor.ExecutorAction;
import org.jazzteam.service.PerformerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformerServiceImpl implements PerformerService {
    @Value("${message.broker.performer.exchange.name}")
    private String exchangeName;

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Message listener registered @org.jazzteam.config.RabbitMqConfig
     *
     * @param executorAction execute logic
     */
    public void handleMessage(ExecutorAction executorAction) {

    }
}

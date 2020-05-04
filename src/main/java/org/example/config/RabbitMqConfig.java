package org.example.config;

import org.example.service.PerformerService;
import org.example.service.TaskService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Message Broker configuration
 */
@Configuration
public class RabbitMqConfig {

    @Value("${message.broker.task.exchange.name}")
    private String taskExchangeName;

    @Value("${message.broker.performer.exchange.name}")
    private String performerExchangeName;

    private String taskQueueName = "task" + UUID.randomUUID().toString();
    private String performerQueueName = "executor" + UUID.randomUUID().toString();

    @Bean
    Queue taskQueue() {
        return new Queue(taskQueueName, false);
    }

    @Bean
    Queue performerQueue() {
        return new Queue(performerQueueName, false);
    }

    /**
     * org.springframework.amqp.core.FanoutExchange: producing message for every consumer
     *
     * @return simple org.springframework.amqp.core.FanoutExchange bean
     */
    @Bean
    FanoutExchange taskExchange() {
        return new FanoutExchange(taskExchangeName);
    }

    @Bean
    FanoutExchange performerExchange() {
        return new FanoutExchange(performerExchangeName);
    }

    @Bean
    Binding taskQueueBinding(Queue taskQueue, FanoutExchange taskExchange) {
        return BindingBuilder.bind(taskQueue).to(taskExchange);
    }

    @Bean
    Binding performerQueueBinding(Queue performerQueue, FanoutExchange performerExchange) {
        return BindingBuilder.bind(performerQueue).to(performerExchange);
    }

    @Bean
    SimpleMessageListenerContainer taskContainer(ConnectionFactory connectionFactory,
                                                 MessageListenerAdapter taskListenerAdapter) {
        return createContainer(connectionFactory, taskListenerAdapter, taskQueueName);
    }

    @Bean
    SimpleMessageListenerContainer performerContainer(ConnectionFactory connectionFactory,
                                                      MessageListenerAdapter performerListenerAdapter) {
        return createContainer(connectionFactory, performerListenerAdapter, performerQueueName);
    }

    @Bean
    MessageListenerAdapter taskListenerAdapter(TaskService taskService) {
        // MessageListenerAdapter.defaultListenerMethod = "handleMessage"
        return new MessageListenerAdapter(taskService);
    }

    @Bean
    MessageListenerAdapter performerListenerAdapter(PerformerService performerService) {
        // MessageListenerAdapter.defaultListenerMethod = "handleMessage"
        return new MessageListenerAdapter(performerService);
    }

    private SimpleMessageListenerContainer createContainer(
            ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter, String queueName) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }
}

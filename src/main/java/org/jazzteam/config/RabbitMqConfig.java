package org.jazzteam.config;

import org.jazzteam.gui.MainForm;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * Message Broker configuration
 */
@Configuration
public class RabbitMqConfig {

    @Value("${message.broker.exchange.name}")
    private String exchangeName;

    private String queueName = UUID.randomUUID().toString();

    @Bean
    public Queue testAppQueue() {
        return new Queue(queueName, false);
    }

    /**
     * org.springframework.amqp.core.FanoutExchange: producing message for every consumer
     *
     * @return simple org.springframework.amqp.core.FanoutExchange bean
     */
    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(exchangeName);
    }

    @Bean
    public Binding testAppBinding(Queue testAppQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(testAppQueue).to(exchange);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(MainForm receiver) {
        // MessageListenerAdapter.defaultListenerMethod = "handleMessage"
        return new MessageListenerAdapter(receiver);
    }

    /**
     * Creates queues on startup instead of first producer
     */
    @Bean
    public InitializingBean prepareQueues(AmqpAdmin amqpAdmin) {
        return () -> {
            Queue queue = testAppQueue();
            FanoutExchange exchange = exchange();
            Binding binding = testAppBinding(queue, exchange);
            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareExchange(exchange);
            amqpAdmin.declareBinding(binding);
        };
    }
}

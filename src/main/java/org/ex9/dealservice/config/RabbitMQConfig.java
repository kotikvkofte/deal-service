package org.ex9.dealservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация RabbitMQ.
 * <p>
 * Определяет все необходимые {@link org.springframework.amqp.core.Exchange},
 * {@link org.springframework.amqp.core.Queue} и {@link org.springframework.amqp.core.Binding}
 * для поддержки обработки сообщений о контрагентах,
 * а также обработку «мертвых» сообщений и реализацию retry-политики.
 * </p>
 * @author Краковцев Артём
 */
@Configuration
public class RabbitMQConfig {

    //exchanges
    @Value("${spring.rabbitmq.exchanges.contractor}")
    private String contractorsExchange;

    @Value("${spring.rabbitmq.exchanges.dead}")
    private String dealsDeadExchange;

    @Value("${spring.rabbitmq.exchanges.retry}")
    private String dealsRetryExchange;

    //queues
    @Value("${spring.rabbitmq.queues.contractor}")
    private String dealContractorsQueue;

    @Value("${spring.rabbitmq.queues.dead}")
    private String dealsContractorDeadQueue;

    //routing keys
    @Value("${spring.rabbitmq.routing-keys.contractor}")
    private String dealContractorsRoutingKey;

    @Value("${spring.rabbitmq.routing-keys.dead}")
    private String dealsDeadRoutingKey;

    @Value("${spring.rabbitmq.routing-keys.retry}")
    private String dealsRetryRoutingKey;

    /**Конвертер для десериализации payload из сообщения*/
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**Контейнер ручным подтверждением сообщений (MANUAL ACK)*/
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    //contractor
    /**Обменник, в который публикует сервис контрагентов*/
    @Bean
    public TopicExchange contractorsExchange() {
        return new TopicExchange(contractorsExchange, true, false);
    }

    /**Основная очередь сервиса сделок*/
    @Bean
    public Queue dealsQueue() {
        return QueueBuilder.durable(dealContractorsQueue)
                .withArgument("x-dead-letter-exchange", dealsDeadExchange)
                .withArgument("x-dead-letter-routing-key", dealsDeadRoutingKey)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(dealsQueue())
                .to(contractorsExchange())
                .with(dealContractorsRoutingKey);
    }

    //dead
    /**dead-letter обменник*/
    @Bean
    public TopicExchange dealsDeadExchange() {
        return new TopicExchange(dealsDeadExchange, true, false);
    }

    /**dead-letter очередь*/
    @Bean
    public Queue dealsContractorDeadQueue() {
        return QueueBuilder.durable(dealsContractorDeadQueue)
                .withArgument("x-message-ttl", 300000)
                .withArgument("x-dead-letter-exchange", dealsRetryExchange)
                .withArgument("x-dead-letter-routing-key", dealsRetryRoutingKey)
                .build();
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(dealsContractorDeadQueue())
                .to(dealsDeadExchange())
                .with(dealsDeadRoutingKey);
    }

    //retry
    /**Retry-обменник, из которого сообщение возвращается в рабочую очередь*/
    @Bean
    public TopicExchange dealsRetryExchange() {
        return new TopicExchange(dealsRetryExchange, true, false);
    }

    @Bean
    public Binding retryBinding() {
        return BindingBuilder.bind(dealsQueue())
                .to(dealsRetryExchange())
                .with(dealsRetryRoutingKey);
    }

}

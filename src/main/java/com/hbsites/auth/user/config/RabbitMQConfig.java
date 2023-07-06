package com.hbsites.auth.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String USER_REQUEST_QUEUE = "user.request";
    public static final String USER_RESPONSE_QUEUE = "user.response";
    public static final String USER_EXCHANGE = "user-exchange";

    @Bean
    Queue msgQueue() {
        return new Queue(USER_REQUEST_QUEUE);
    }

    @Bean
    Queue replyQueue() {
        return new Queue(USER_RESPONSE_QUEUE);
    }

    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    Binding msgBinding() {
        return BindingBuilder.bind(msgQueue())
                .to(topicExchange())
                .with(USER_REQUEST_QUEUE);
    }

    @Bean
    Binding replyBinding() {
        return BindingBuilder.bind(replyQueue())
                .to(topicExchange())
                .with(USER_RESPONSE_QUEUE);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

package com.hbsites.auth.user.config;

import com.hbsites.hbsitescommons.queues.RabbitQueues;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    DirectExchange userExchange() {
        return new DirectExchange(RabbitQueues.USER_EXCHANGE);
    }
    @Bean
    DirectExchange coreExchange() {
        return new DirectExchange(RabbitQueues.RPGTRACKER_CORE_EXCHANGE);
    }
    @Bean
    DirectExchange cocExchange() {
        return new DirectExchange(RabbitQueues.RPGTRACKER_COC_EXCHANGE);
    }


    @Bean
    Queue msgQueue() {
        return new Queue(RabbitQueues.USER_REQUEST_QUEUE);
    }
    @Bean
    Queue replyCoreQueue() {
        return new Queue(RabbitQueues.CORE_USER_RESPONSE_QUEUE);
    }
    @Bean
    Queue replyCoCQueue() {
        return new Queue(RabbitQueues.COC_USER_RESPONSE_QUEUE);
    }

    @Bean
    Binding msgBinding() {
        return BindingBuilder.bind(msgQueue())
                .to(userExchange())
                .with(RabbitQueues.USER_REQUEST_QUEUE);
    }

    @Bean
    Binding replyCoCBinding() {
        return BindingBuilder.bind(replyCoCQueue())
                .to(cocExchange())
                .with(RabbitQueues.COC_USER_RESPONSE_QUEUE);
    }

    @Bean
    Binding replyCoreBinding() {
        return BindingBuilder.bind(replyCoreQueue())
                .to(coreExchange())
                .with(RabbitQueues.CORE_USER_RESPONSE_QUEUE);
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

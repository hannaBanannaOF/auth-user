package com.hbsites.auth.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String USER_REQUEST_QUEUE = "user.request";
    public static final String USER_RESPONSE_QUEUE = "user.response";
    public static final String USER_EXCHANGE = "user-exchange";
    /** *
     * Configure the Send Message Queue
     */
    @Bean
    Queue msgQueue() {
        return new Queue(USER_REQUEST_QUEUE);
    }
    /** *
     * Return Queue Configuration
     */
    @Bean
    Queue replyQueue() {
        return new Queue(USER_RESPONSE_QUEUE);
    }
    /** *
     * Switch setting
     */
    @Bean
    TopicExchange topicExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }
    /** *
     * Queuing and Switch Link Request
     */
    @Bean
    Binding msgBinding() {
        return BindingBuilder.bind(msgQueue())
                .to(topicExchange())
                .with(USER_REQUEST_QUEUE);
    }
    /** *
     * Back to Queue and Switch Link
     */
    @Bean
    Binding replyBinding() {
        return BindingBuilder.bind(replyQueue())
                .to(topicExchange())
                .with(USER_RESPONSE_QUEUE);
    }
}

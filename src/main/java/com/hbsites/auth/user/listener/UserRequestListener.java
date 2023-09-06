package com.hbsites.auth.user.listener;

import com.hbsites.auth.user.config.RabbitMQConfig;
import com.hbsites.auth.user.service.KeycloakService;
import com.hbsites.hbsitescommons.messages.UUIDListPayload;
import com.hbsites.hbsitescommons.dto.UserDTO;
import com.hbsites.hbsitescommons.messages.UserDTOListPayload;
import com.hbsites.hbsitescommons.queues.RabbitQueues;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRequestListener {

    @Lazy
    @Autowired
    private KeycloakService keycloakService;
    @Lazy
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitQueues.USER_REQUEST_QUEUE)
    public void process(UUIDListPayload message) {
        List<UserDTO> users = keycloakService.getPlayerByUuid(message.uuids());

        UserDTOListPayload build = new UserDTOListPayload(users, message.userRequested(), message.session(), message.characterSheet());
        rabbitTemplate.convertAndSend(message.microservice().getExchange(), message.microservice().getUserReplyQueue(), build);
    }
}

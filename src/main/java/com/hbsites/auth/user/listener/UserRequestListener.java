package com.hbsites.auth.user.listener;

import com.hbsites.auth.user.config.RabbitMQConfig;
import com.hbsites.auth.user.service.KeycloakService;
import com.hbsites.hbsitescommons.dto.UUIDListPayload;
import com.hbsites.hbsitescommons.dto.UserDTO;
import com.hbsites.hbsitescommons.dto.UserDTOListPayload;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserRequestListener {

    @Lazy
    @Autowired
    private KeycloakService keycloakService;
    @Lazy
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.USER_REQUEST_QUEUE)
    public void process(Message message) {
        byte[] body = message.getBody();
        UUIDListPayload userIds = (UUIDListPayload) SerializationUtils.deserialize(body);
        List<UserDTO> users = keycloakService.getPlayerByUuid(userIds.getUuids());

        //This is the message to be returned by the server
        Message build = MessageBuilder.withBody(SerializationUtils.serialize(new UserDTOListPayload(users))).build();
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getCorrelationId());
        rabbitTemplate.sendAndReceive(RabbitMQConfig.USER_EXCHANGE, RabbitMQConfig.USER_RESPONSE_QUEUE, build, correlationData);
    }
}

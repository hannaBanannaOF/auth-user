package com.hbsites.auth.user.listener;

import com.hbsites.auth.user.service.KeycloakService;
import com.hbsites.hbsitescommons.commons.dto.UserDTO;
import com.hbsites.hbsitescommons.commons.messages.UUIDListPayload;
import com.hbsites.hbsitescommons.commons.messages.UserDTOListPayload;
import com.hbsites.hbsitescommons.commons.queues.RabbitQueues;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class UserRequestListener {

    @Lazy
    @Autowired
    private KeycloakService keycloakService;
    @Lazy
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitQueues.USER_REQUEST_QUEUE)
    public void process(UUIDListPayload message) {
        log.info("[USER-REQUEST] - Received message: %s".formatted(message.toString()));
        List<UserDTO> users = keycloakService.getPlayerByUuid(message.uuids());

        UserDTOListPayload build = new UserDTOListPayload(users, message.userRequested(), message.session(), message.characterSheet());
        log.info("[USER-REQUEST] - Sending message to RabbitMQ (%s:%s): %s".formatted(message.microservice().getExchange(), message.microservice().getUserReplyQueue(), build.toString()));
        rabbitTemplate.convertAndSend(message.microservice().getExchange(), message.microservice().getUserReplyQueue(), build);
    }
}

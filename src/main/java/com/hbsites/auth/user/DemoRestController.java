package com.hbsites.auth.user;

import com.hbsites.auth.user.service.KeycloakService;
import com.hbsites.hbsitescommons.config.ApiVersion;
import com.hbsites.hbsitescommons.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@ApiVersion(1)
public class DemoRestController {

    @Lazy
    @Autowired
    private KeycloakService keycloakService;

    @GetMapping("/user")
    public List<UserDTO> getUsers() {
        return keycloakService.getPlayerByUuid(List.of(UUID.fromString("9af40220-4bf3-4e42-90f6-9f24c449aac4")));
    }
}

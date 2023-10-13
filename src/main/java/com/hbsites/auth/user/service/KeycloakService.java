package com.hbsites.auth.user.service;

import com.hbsites.hbsitescommons.commons.dto.UserDTO;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakService {
    private final String REALM = "HBsites";
    private final Keycloak kc;

    public KeycloakService() {
        kc = KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080/")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(REALM)
                .clientId("keycloak-admin")
                .clientSecret("IimlWGf2LTK5LCLf3JEWuJMCSv2MTK59")
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();
    }

    public List<UserDTO> getPlayerByUuid(List<UUID> idList) {
        List<UserDTO> result = new ArrayList<>();
        try {
            kc.tokenManager().getAccessToken();
            UsersResource resource = kc.realm(REALM).users();
            for (UUID id : idList) {
                UserDTO player = new UserDTO();
                UserResource userResource = resource.get(id.toString());
                UserRepresentation  representation;
                try {
                    representation = userResource != null ? userResource.toRepresentation() : null;
                } catch(NotFoundException e) {
                    e.printStackTrace();
                    continue;
                }

                if (representation != null) {
                    player.setUuid(id);
                    if (representation.getFirstName() != null) {
                        player.setFirstName(representation.getFirstName());
                    }
                    if (representation.getLastName() != null) {
                        player.setLastName(representation.getLastName());
                    }
                    if (representation.getUsername() != null) {
                        player.setUserName(representation.getUsername());
                    }
                    result.add(player);
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
        }
        kc.tokenManager().logout();
        return result;
    }
}

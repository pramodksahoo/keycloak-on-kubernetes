package org.bhn.model.event;

import lombok.Data;

@Data
public class KeycloakMessage {
    private String email;
    private String personId;
    private SingUpLoginMessage message;
}

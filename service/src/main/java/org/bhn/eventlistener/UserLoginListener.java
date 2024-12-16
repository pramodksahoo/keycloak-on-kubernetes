package org.bhn.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.bhn.message.MessageQueue;
import org.bhn.model.event.KeycloakMessage;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

@Slf4j
public class UserLoginListener extends AbstractListener {

    public UserLoginListener(KeycloakSession session, MessageQueue messageQueue) {
        super(session, messageQueue);
    }

    @Override
    public void onEvent(Event event) {
        if (EventType.LOGIN.equals(event.getType())) {
            super.onEvent(event);
            log.info("New Login Event for user with id: {}", event.getUserId());
            UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            KeycloakMessage message = createKeycloakMessage(event, user);
            log.info("Sending keycloak message {}", message);
            messageQueue.sendMessage(message);
        }
    }
}

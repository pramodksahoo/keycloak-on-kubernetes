package org.bhn.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.bhn.message.MessageQueue;
import org.bhn.model.event.KeycloakMessage;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.models.*;

/**
 * @author Vinod Atwal
 */
@Slf4j
public class UserRegisterListener extends AbstractListener {

	public UserRegisterListener(KeycloakSession session, MessageQueue messageQueue) {
		super(session, messageQueue);
	}

	@Override
	public void onEvent(Event event) {
		if (EventType.REGISTER.equals(event.getType())) {
			super.onEvent(event);
			log.info("New REGISTER event for user: {}", event.getUserId());
			UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
			KeycloakMessage message = createKeycloakMessage(event, user);
			log.info("Sending keycloak message {}", message);
			messageQueue.sendMessage(message);
		}
	}

}

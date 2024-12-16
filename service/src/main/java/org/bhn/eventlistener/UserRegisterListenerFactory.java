package org.bhn.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.bhn.message.MessageQueue;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

/**
 * @author Vinod Atwal
 *
 */
@Slf4j
public class UserRegisterListenerFactory implements EventListenerProviderFactory {

	public static final String PROVIDER_ID = "reg-listener";

	@Override
	public EventListenerProvider create(KeycloakSession session) {
		return new UserRegisterListener(session, new MessageQueue());
	}

	@Override
	public void init(Config.Scope config) {
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}

	@Override
	public String getId() {
		return PROVIDER_ID;
	}
}

package org.bhn.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.bhn.model.CustomConstant;
import org.bhn.model.personDTO.PersonResponseDTO;
import org.bhn.resource.services.CreatePersonService;
import org.bhn.resource.services.impl.CreatePersonServiceImpl;
import org.keycloak.events.Details;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;
import javax.ws.rs.core.MultivaluedMap;
import java.net.MalformedURLException;

/**
 * @author bthiy00
 */
@Slf4j
public class CreatePersonListener implements EventListenerProvider {

	private final KeycloakSession session;
	private final CreatePersonService personService;

	public CreatePersonListener(KeycloakSession session) {
		this.session = session;
		this.personService = new CreatePersonServiceImpl(session);
	}

	@Override
	public void onEvent(Event event) {
		if (EventType.REGISTER.equals(event.getType())) {

			UserModel user = session.users().getUserById(session.getContext().getRealm(), event.getUserId());
            try {

				log.info("Create person for registered user: {}", event.getUserId());

				PersonResponseDTO response = personService.createPerson(user, null);

				log.info("person created for registered user: {}", response.getPersonId());
				if(event.getDetails().get(Details.REGISTER_METHOD).equals("form")) {
					MultivaluedMap<String, String> formData = session.getContext().getHttpRequest().getDecodedFormParameters();
					formData.add(CustomConstant.ATTRIBUTE_PERSON_ID, response.getPersonId());
				} else {
					session.getContext().getAuthenticationSession().getAuthenticatedUser().setSingleAttribute("personId", response.getPersonId());
				}

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

		}
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {

	}

	@Override
	public void close() {

	}
}

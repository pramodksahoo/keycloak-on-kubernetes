package org.bhn.eventlistener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bhn.message.MessageQueue;
import org.bhn.model.event.KeycloakMessage;
import org.bhn.model.event.SingUpLoginMessage;
import org.bhn.utils.Utils;
import org.keycloak.device.DeviceRepresentationProvider;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.representations.account.DeviceRepresentation;
import ua_parser.Client;

import javax.ws.rs.core.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

import static org.bhn.model.CustomConstant.PERSON_ID;

@Slf4j
public abstract class AbstractListener implements EventListenerProvider {
    protected final KeycloakSession session;
    protected final MessageQueue messageQueue;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected AbstractListener(KeycloakSession session, MessageQueue messageQueue) {
        this.session = session;
        this.messageQueue = messageQueue;
    }

    protected KeycloakMessage createKeycloakMessage(Event event, UserModel user) {

        KeycloakMessage message = new KeycloakMessage();
        message.setEmail(user.getEmail());

        if (CollectionUtils.isNotEmpty(user.getAttributes().get(PERSON_ID))) {
            message.setPersonId(user.getAttributes().get(PERSON_ID).get(0));
        }

        HttpHeaders headers = session.getContext().getHttpRequest().getHttpHeaders();

        SingUpLoginMessage singUpLoginMessage = new SingUpLoginMessage();
        singUpLoginMessage.setDisplayLanguage(Utils.getDisplayLanguage(headers, "Accept-Language"));
        singUpLoginMessage.setEventType(event.getType().name());
        singUpLoginMessage.setRealmName(session.getContext().getRealm().getName());
        singUpLoginMessage.setClientId(event.getClientId());
        singUpLoginMessage.setCreationDate(user.getCreatedTimestamp());
        singUpLoginMessage.setFirstName(user.getFirstName());
        singUpLoginMessage.setLastName(user.getLastName());
        singUpLoginMessage.setExternalUserId(user.getId());
        singUpLoginMessage.setEmailVerified(user.isEmailVerified());
        singUpLoginMessage.setIsSocial(isSocial(user, event));
        singUpLoginMessage.setAppDomain(event.getClientId());


        if (EventType.LOGIN.equals(event.getType())) {

            Map<String, String> clientAttributes = new HashMap<>();

            boolean newDeviceLoginNotificationEmail = true;

            ProtocolMapperModel protocolMapperModel = session.getContext().getClient().getProtocolMapperByName(OIDCLoginProtocol.LOGIN_PROTOCOL, "newDeviceLoginNotificationEmail");
            if (protocolMapperModel != null) {
                newDeviceLoginNotificationEmail = Boolean.parseBoolean(protocolMapperModel.getConfig().get("claim.value"));
            }

            clientAttributes.put("newDeviceLoginNotificationEmail", String.valueOf(newDeviceLoginNotificationEmail));

            singUpLoginMessage.setClientAttributes(clientAttributes);

        }

        message.setMessage(singUpLoginMessage);

        DeviceRepresentation deviceRepresentation = session.getProvider(DeviceRepresentationProvider.class).deviceRepresentation();

        singUpLoginMessage.setLastIp(deviceRepresentation.getIpAddress());
        singUpLoginMessage.setDeviceName(deviceRepresentation.getDevice());
        singUpLoginMessage.setDeviceOs(deviceRepresentation.getOs());
        singUpLoginMessage.setDeviceOsMajor(deviceRepresentation.getOsVersion());
        singUpLoginMessage.setDeviceUserAgent(deviceRepresentation.getBrowser());
        singUpLoginMessage.setPromotionEmail(false);
        if (user.getAttributes().get("promotionEmail") != null && user.getAttributes().get("promotionEmail").contains(event.getClientId())) {
            singUpLoginMessage.setPromotionEmail(true);
        }
        return message;
    }

    private boolean isSocial(UserModel user, Event event) {
        boolean isSocialLogin = isSocial(user);
        boolean isCredentialLogin = false;
        if (event.getDetails() != null) {
            isCredentialLogin = event.getDetails().containsKey("auth_method");
        }
        return isSocialLogin && !isCredentialLogin;
    }

    private boolean isSocial(UserModel user) {
        RealmModel realm = session.getContext().getRealm();
        return session.users().getFederatedIdentitiesStream(realm, user).anyMatch(identity -> {
            IdentityProviderModel identityProvider = realm.getIdentityProviderByAlias(identity.getIdentityProvider());
            return identityProvider != null && identityProvider.isEnabled();
        });
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
    }

    @Override
    public void onEvent(Event event){
        log.info("Received Keycloak Event: {}", eventToString(event));
    }


    @Override
    public void close() {

    }

    private String eventToString(Event event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert event to JSON string", e);
            return "Failed to convert event to JSON string";
        }
    }
}

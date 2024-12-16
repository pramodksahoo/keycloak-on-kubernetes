package org.bhn.resource;

import com.google.auto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.bhn.auth.AuthEvaluator;
import org.bhn.resource.utils.TokenValidator;
import org.keycloak.Config;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
@Slf4j
public class UsersResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String PROVIDER_ID = "users";

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        log.info("creating UserResourceProviderFactory under context of {} realm",keycloakSession.getContext().getRealm().getName());
        String clientId = keycloakSession.getContext().getRequestHeaders().getHeaderString("ClientId");
        ClientModel clientModel = keycloakSession.getContext().getRealm().getClientByClientId(clientId);
        keycloakSession.getContext().setClient(clientModel);

        if (keycloakSession.getAttribute("token") == null ) {
            keycloakSession.setAttribute("token", TokenValidator.validateToken(keycloakSession));
        }
        if(keycloakSession.getAttribute("auth") == null){
            keycloakSession.setAttribute("auth", new AuthEvaluator(keycloakSession, keycloakSession.getContext().getRealm()));
        }
        return new UsersResourceProvider(keycloakSession);

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

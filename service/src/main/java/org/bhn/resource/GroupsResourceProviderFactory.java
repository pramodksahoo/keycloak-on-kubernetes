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
public class GroupsResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String PROVIDER_ID = "groups";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {

        String clientId = session.getContext().getRequestHeaders().getHeaderString("ClientId");
        ClientModel clientModel = session.getContext().getRealm().getClientByClientId(clientId);
        session.getContext().setClient(clientModel);

        if (session.getAttribute("token") == null ) {
            session.setAttribute("token", TokenValidator.validateToken(session));
        }
        if(session.getAttribute("auth") == null){
            session.setAttribute("auth", new AuthEvaluator(session, session.getContext().getRealm()));
        }
        return new GroupsResourceProvider(session);
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

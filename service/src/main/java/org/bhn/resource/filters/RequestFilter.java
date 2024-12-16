package org.bhn.resource.filters;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.CorsHeaders;
import org.keycloak.OAuthErrorException;
import org.keycloak.common.util.Resteasy;
import org.keycloak.events.Errors;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.CorsErrorResponseException;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.services.resources.Cors;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Set;

@NoCache
@Provider
@Slf4j
public class RequestFilter implements ContainerRequestFilter {

    private Cors cors;
    private KeycloakSession session;
    private RealmModel realm;
    private ClientModel client;
    private String origin;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {


        UriInfo info = requestContext.getUriInfo();
        String extName = info.getPathParameters().getFirst("extension");

        if (extName != null && extName.equals("user")) {

            session = Resteasy.getContextData(KeycloakSession.class);

            cors = Cors.add(session.getContext().getHttpRequest()).auth().allowedMethods("POST").auth().exposedHeaders(Cors.ACCESS_CONTROL_ALLOW_METHODS);

            realm = session.getContext().getRealm();

            origin = requestContext.getHeaders().getFirst(CorsHeaders.ORIGIN);

            String clientId = requestContext.getHeaders().getFirst("clientId");

            checkClient(clientId);
            checkSsl();
            checkRealm();

            if (!client.isServiceAccountsEnabled()) {
                checkOrigin();
            }

        }
    }

    private void checkOrigin() {
        Set<String> allowedOrigins = client.getWebOrigins();


        log.info("Requesting origin {}", origin);

        if (allowedOrigins == null || (!allowedOrigins.contains("*") && !allowedOrigins.contains(origin))) {
            throw new ErrorResponseException(OAuthErrorException.UNAUTHORIZED_CLIENT, "invalid origin", Response.Status.UNAUTHORIZED);
        }
    }

    private void checkClient(String clientId) {

        if (clientId == null) {
            throw new ErrorResponseException(Errors.UNAUTHORIZED_CLIENT, "invalid client", Response.Status.UNAUTHORIZED);
        }

        client = session.getContext().getRealm().getClientByClientId(clientId);
        if (client == null) {
            throw new ErrorResponseException(Errors.UNAUTHORIZED_CLIENT, "invalid client", Response.Status.UNAUTHORIZED);
        }

        if (!client.isEnabled()) {
            throw new ErrorResponseException(Errors.UNAUTHORIZED_CLIENT, "invalid client", Response.Status.UNAUTHORIZED);
        }


        /*if (clientModel.isConsentRequired()) {
            throw new CorsErrorResponseException(cors, OAuthErrorException.INVALID_CLIENT, "Client requires user consent", Response.Status.BAD_REQUEST);
        }*/

        /*not checking for isDirectAccessEnable or not
        if (!client.isDirectAccessGrantsEnabled()) {
            event.error(Errors.NOT_ALLOWED);
            throw new CorsErrorResponseException(cors, OAuthErrorException.UNAUTHORIZED_CLIENT, "Client not allowed for direct access grants", Response.Status.BAD_REQUEST);
        }*/

//        session.getContext().setClient(client);


//        EventBuilder eventBuilder = new EventBuilder(realm, session, session.getContext().getConnection());
//        eventBuilder.event(EventType.CLIENT_LOGIN);
//        session.setAttribute("client_id", clientId);
//        AuthorizeClientUtil.ClientAuthResult clientAuthResult = AuthorizeClientUtil.authorizeClient(session, eventBuilder , cors);
//        client = clientAuthResult.getClient();
    }

    private void checkSsl() {
        if (!session.getContext().getUri().getBaseUri().getScheme().equals("https") && session.getContext().getRealm().getSslRequired().isRequired(session.getContext().getConnection())) {
            throw new CorsErrorResponseException(cors.allowAllOrigins(), OAuthErrorException.INVALID_REQUEST, "HTTPS required", Response.Status.FORBIDDEN);
        }
    }

    private void checkRealm() {
        if (!realm.isEnabled()) {
            throw new CorsErrorResponseException(cors.allowAllOrigins(), "access_denied", "Realm not enabled", Response.Status.FORBIDDEN);
        }
    }
}

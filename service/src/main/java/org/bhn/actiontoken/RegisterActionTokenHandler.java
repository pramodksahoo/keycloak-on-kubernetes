package org.bhn.actiontoken;

import com.google.auto.service.AutoService;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.authentication.actiontoken.AbstractActionTokenHandler;
import org.keycloak.authentication.actiontoken.ActionTokenContext;
import org.keycloak.authentication.actiontoken.ActionTokenHandlerFactory;
import org.keycloak.authentication.actiontoken.TokenUtils;
import org.keycloak.events.Errors;
import org.keycloak.events.EventType;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.JsonWebToken;
import org.keycloak.services.clientregistration.RegistrationAccessToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.LoginActionsService;
import org.keycloak.services.util.ResolveRelative;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

import static org.bhn.resource.constants.Constants.REGISTER_ACTION_TOKEN;

@AutoService(ActionTokenHandlerFactory.class)
public class RegisterActionTokenHandler extends AbstractActionTokenHandler<RegisterActionToken> {

    public RegisterActionTokenHandler() {
        super(
                RegisterActionToken.TOKEN_TYPE,
                RegisterActionToken.class,
                Messages.INVALID_REQUEST,
                EventType.EXECUTE_ACTION_TOKEN,
                Errors.INVALID_REQUEST
        );
    }

    @Override
    public Response handleToken(RegisterActionToken token, ActionTokenContext<RegisterActionToken> tokenContext) {
        KeycloakSession session = tokenContext.getSession();
        RealmModel realm = tokenContext.getRealm();
        AuthenticationSessionModel authSession = tokenContext.getAuthenticationSession();
        ClientModel client = authSession.getClient();

        String redirectUri = token.getRedirectUri() != null
                ? token.getRedirectUri()
                : ResolveRelative.resolveRelativeUri(session, client.getRootUrl(), client.getBaseUrl());

        String redirect = RedirectUtils.verifyRedirectUri(session, redirectUri, client);

        if (redirect != null) {
            authSession.setAuthNote(
                    AuthenticationManager.SET_REDIRECT_URI_AFTER_REQUIRED_ACTIONS, Boolean.TRUE.toString());
            authSession.setRedirectUri(redirect);
            authSession.setClientNote(OIDCLoginProtocol.REDIRECT_URI_PARAM, redirectUri);
            if (token.getState() != null) {
                authSession.setClientNote(OIDCLoginProtocol.STATE_PARAM, token.getState());
            }
//            if (token.getActionVerificationNonce() != null) {
//                authSession.setClientNote(OIDCLoginProtocol.NONCE_PARAM, token.getActionVerificationNonce().toString());
//            }
        }

        if (token.getScope() != null) {
            authSession.setClientNote(OAuth2Constants.SCOPE, token.getScope());
            AuthenticationManager.setClientScopesInSession(authSession);
        }
        authSession.getAuthenticatedUser().setEmailVerified(true);
        try {
            AuthenticationProcessor processor = new AuthenticationProcessor();
            processor.setSession(session);
            processor.setAuthenticationSession(authSession);
            processor.setClient(authSession.getClient());
            processor.setRequest(session.getContext().getHttpRequest());
            processor.setUriInfo(session.getContext().getUri());
            processor.setRealm(realm);
            processor.setFlowPath(LoginActionsService.AUTHENTICATE_PATH);
            AuthenticationFlowModel regFlow = realm.getFlowByAlias("complete-registration");
            processor.setFlowId(regFlow.getId());
            processor.setBrowserFlow(false);
            processor.setEventBuilder(tokenContext.getEvent());
            return processor.authenticate();
        } catch (Exception e) {
            tokenContext.getEvent().error("authentication_failed");
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @Override
    public TokenVerifier.Predicate<? super RegisterActionToken>[] getVerifiers(ActionTokenContext<RegisterActionToken> tokenContext) {

        return TokenUtils.predicates(
                TokenUtils.checkThat(
                        t -> tokenContext.getAuthenticationSession().getAuthenticatedUser().getEmail().equalsIgnoreCase(t.getEmail()),
                        Errors.INVALID_EMAIL, getDefaultErrorMessage()
                ),
                TokenUtils.checkThat(t-> RegisterActionToken.TOKEN_TYPE.equalsIgnoreCase(t.getType()),Errors.INVALID_TOKEN_TYPE,getDefaultErrorMessage())
//                TokenUtils.checkThat(t-> tokenContext.getAuthenticationSession().getAuthNote("ACTION_TOKEN_TYPE")!=null && tokenContext.getAuthenticationSession().getAuthNote("ACTION_TOKEN_TYPE").equalsIgnoreCase(REGISTER_ACTION_TOKEN),"INVALID SESSION","invalid Session")
        );

    }

    @Override
    public int order() {
        return super.order();
    }

    @Override
    public List<ProviderConfigProperty> getConfigMetadata() {
        return super.getConfigMetadata();
    }

    @Override
    public AuthenticationSessionModel startFreshAuthenticationSession(
            RegisterActionToken token, ActionTokenContext<RegisterActionToken> tokenContext) {
        AuthenticationSessionModel authenticationSessionForClient = tokenContext.createAuthenticationSessionForClient(token.getIssuedFor());
        return authenticationSessionForClient;
    }

    @Override
    public boolean canUseTokenRepeatedly(RegisterActionToken token, ActionTokenContext<RegisterActionToken> tokenContext) {
        return token.isReuse();
    }
}

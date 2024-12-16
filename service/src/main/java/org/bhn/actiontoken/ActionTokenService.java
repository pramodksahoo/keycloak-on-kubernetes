package org.bhn.actiontoken;

import org.bhn.constants.ResponseCode;
import org.bhn.resource.exception.ErrorResponseException;
import org.bhn.resource.handler.ResponseBuilder;
import org.bhn.resource.services.EmailSender;
import org.keycloak.Config;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.util.Time;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import org.keycloak.services.Urls;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bhn.constants.ResponseCode.*;

public class ActionTokenService {
    private final KeycloakSession session;
    private final RealmModel realm;
    private  ClientModel client;

    public ActionTokenService(KeycloakSession session) {
        this.session = session;
        this.realm = session.getContext().getRealm();
    }

    public Response createActionToken(ActionTokenRequest payload){
        checkAuth(session);
        List<FormMessage> errors = validateToken(payload);
        if(!errors.isEmpty()){
            throw new ErrorResponseException("Invalid request", Response.Status.BAD_REQUEST, errors);
        }

        String redirectUri = payload.getRedirectUri();
        ClientModel clientModel = session.clients().getClientByClientId(realm, payload.getClientId());
        if(clientModel == null){
            throw new ErrorResponseException("Invalid requested client", Response.Status.BAD_REQUEST,AUTH_ERR_1015);
        }
        client=clientModel;
        if (!validateRedirectUri(this.client, redirectUri)) {
            throw new ErrorResponseException("Invalid redirect uri", Response.Status.BAD_REQUEST,AUTH_ERR_1016);
        }

        String emailOrUsername = payload.getEmail();
        boolean forceCreate = payload.isForceCreate();
        if (payload.getUsername() != null) {
            emailOrUsername = payload.getUsername();
            forceCreate = false;
        }

        UserModel user = getOrCreateUser(emailOrUsername, forceCreate, payload.getRequiredActions());
        RegisterActionToken token = createActionToken(
                user, this.client.getClientId(), payload.getExpirationSeconds(), redirectUri, payload.isReuse(),
                payload.getScope(), payload.getNonce(), payload.getState());

        String tokenUrl = getActionTokenUrl(token);

        ActionTokenResponse response = new ActionTokenResponse();
        response.setUserId(user.getId());
//        response.setLink(tokenUrl);
        String exp = payload.getExpirationSeconds()==null?"1 day": String.format("%s minutes", payload.getExpirationSeconds() / 60);
        EmailSender.sendInvitationRegEmail(client,user.getEmail(),tokenUrl,exp);
        return new ResponseBuilder(Response.Status.OK, "ActionToken sent successFully", ResponseCode.AUTH_1011,response).build();

    }
    private boolean validateRedirectUri(ClientModel client, String redirectUri) {
        String redirect = RedirectUtils.verifyRedirectUri(session, redirectUri, client);
        return redirectUri.equals(redirect);
    }

    private UserModel getOrCreateUser(String email, boolean forceCreate,String[] requiredActions) {
        UserModel user = KeycloakModelUtils.findUserByNameOrEmail(session, realm, email);
        if (user == null && forceCreate) {
            user = session.users().addUser(realm, email);
            user.setEnabled(true);
            user.setEmail(email);
            registerEvent(user);
        }
        if (user == null) {
            throw new ErrorResponseException("unable to creat user force create is off", Response.Status.BAD_REQUEST);
        }
        Arrays.stream(requiredActions).forEach(user::addRequiredAction);
        return user;
    }

    private void registerEvent(UserModel user) {
        ClientConnection clientConnection = session.getContext().getConnection();
        EventBuilder eventBuilder = new EventBuilder(realm, session, clientConnection).realm(realm);
        eventBuilder
                .event(EventType.REGISTER)
                .detail(Details.REGISTER_METHOD, RegisterActionToken.TOKEN_TYPE)
                .detail(Details.USERNAME, user.getUsername())
                .detail(Details.EMAIL, user.getEmail())
                .user(user)
                .success();
    }
    private RegisterActionToken createActionToken(UserModel user, String clientId, Integer expiration, String redirectUri,
                                                boolean reuse, String scope, String nonce, String state) {
        int expirationInSecs = (expiration != null && expiration > 0) ? expiration : (60 * 60 * 24);
        int absoluteExpirationInSecs = Time.currentTime() + expirationInSecs;
        return new RegisterActionToken(user.getId(),user.getEmail(), clientId, absoluteExpirationInSecs, reuse, redirectUri, scope, nonce, state);
    }

    private String getActionTokenUrl(RegisterActionToken token) {
        KeycloakContext context = session.getContext();
        UriInfo uriInfo = context.getUri();

        // creating this kind of token for the admin (master) realm is of high risk, thus we don't allow this
        String adminRealm = Config.getAdminRealm();
        if (adminRealm.equals(realm.getName())) {
            throw new ErrorResponseException("This token type is not allowed for realm", Response.Status.BAD_REQUEST);
        }

        // If you are using a different realm to call this method than the one you want to create the action token,
        // we need to temporarily set the session context realm to the latter one, because the SignatureProvider
        // uses the keys from the current sessionContextRealm.
        RealmModel sessionContextRealm = session.getContext().getRealm();
        session.getContext().setRealm(realm);

        // now do the work
        String tokenString = token.serialize(session, realm, uriInfo);
        UriBuilder uriBuilder = Urls.actionTokenBuilder(uriInfo.getBaseUri(), tokenString, token.issuedFor, "");

        // and then reset the realm to the proper one
        session.getContext().setRealm(sessionContextRealm);

        return uriBuilder.build(realm.getName()).toString();
    }

    private List<FormMessage> validateToken(ActionTokenRequest token){
        String email = token.getEmail();
        List<FormMessage> errors = new ArrayList<FormMessage>();
        if(email == null || email.isEmpty()){
            errors.add(new FormMessage("email", "invalid_email"));
        }
        String redirectUri = token.getRedirectUri();
        if(redirectUri == null || redirectUri.isEmpty()){
            errors.add(new FormMessage("redirectUri", "invalid_redirectUri"));
        }
        return errors;

    }
    public static AuthenticationManager.AuthResult checkAuth(KeycloakSession session) {
        AuthenticationManager.AuthResult auth;
        try{
            auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        }catch (Exception e){
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }
        if (auth == null) {
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }else if(!auth.getClient().isServiceAccountsEnabled()){
            throw new ErrorResponseException("Invalid Client", Response.Status.UNAUTHORIZED);
        } else if (auth.getToken().getIssuedFor() == null || !auth.getToken().hasAnyAudience(List.of("keycloak"))) {
            throw new ErrorResponseException("unauthorized client" ,Response.Status.UNAUTHORIZED);
        } else if (!auth.getUser().hasRole(session.roles().getClientRole(
                session.clients().getClientByClientId(
                        session.getContext().getRealm(),"keycloak"
                ),
                "register-invitation-email"
        ))) {
            throw new ErrorResponseException("insufficient role/permission",Response.Status.UNAUTHORIZED);
        }
        return auth;
    }


}

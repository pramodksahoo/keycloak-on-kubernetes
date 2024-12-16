package org.bhn.resource.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bhn.resource.exception.ErrorResponseException;
import org.keycloak.common.util.ServerCookie;
import org.keycloak.http.HttpCookie;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;

@Slf4j
public class CommonUtils {

    private static String PERSON_UPDATE_ROLE;
    private static String ACCOUNT_UPDATE_ROLE = "ACCOUNT_UPDATE";
    private static String ACCOUNT_READ_ROLE = "ACCOUNT_READ";


    public static void removeAuthSession(KeycloakSession session, RealmModel realm, AuthenticationSessionModel authSession) {
        if (authSession==null){
            log.info("auth session is null unable to remove auth session");
            return;
        }
        log.info("removing authSession with id: {}",authSession.getTabId());

        AuthenticationSessionManager authenticationSessionManager = new AuthenticationSessionManager(session);
        authenticationSessionManager.removeTabIdInAuthenticationSession(realm, authSession);
    }

    public static HashMap<String,String> formatInputs(MultivaluedMap<String, String> formParams) {
        HashMap<String,String> parameters = new HashMap<>();

        for(String str : formParams.keySet()){
            parameters.put(str, formParams.getFirst(str));
        }

        return parameters;
    }

    public static AuthenticationManager.AuthResult checkAuth(KeycloakSession session) {
        if(PERSON_UPDATE_ROLE==null){
            PERSON_UPDATE_ROLE= Optional.ofNullable(System.getenv().get("PERSON_UPDATE_ROLE"))
                    .orElseThrow(()->new RuntimeException("environment variable PERSON_UPDATE_ROLE not found"));
        }
        AuthenticationManager.AuthResult auth;
        try{
           auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        }catch (Exception e){
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }

        if (auth == null) {
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        } else if (auth.getToken().getIssuedFor() == null || !auth.getToken().hasAnyAudience(List.of("keycloak"))) {
            throw new ErrorResponseException("unauthorized client" ,Response.Status.UNAUTHORIZED);
        } else if (!auth.getUser().hasRole(session.roles().getClientRole(auth.getClient(), PERSON_UPDATE_ROLE))) {
            throw new ErrorResponseException("insufficient role/permission",Response.Status.UNAUTHORIZED);
        }
        return auth;
    }

    public static ArrayList<HttpCookie> createSignInCookies(KeycloakSession session, AccessTokenResponse token) {

        ArrayList<HttpCookie> cookieList = new ArrayList<>();
        boolean secureOnly = session.getContext().getRealm().getSslRequired().isRequired(session.getContext().getConnection());

        HttpCookie accessTokenCookie  = new HttpCookie(1,"IDENTITY_AUTH_ACCESS_TOKEN", token.getToken(), "/", null, null, -1, secureOnly, false, ServerCookie.SameSiteAttributeValue.NONE);
        HttpCookie idTokenCookie  = new HttpCookie(1,"IDENTITY_AUTH_ID_TOKEN", token.getIdToken(), "/", null, null, -1, secureOnly, false, ServerCookie.SameSiteAttributeValue.NONE);

        cookieList.add(accessTokenCookie);
        cookieList.add(idTokenCookie);

        return cookieList;
    }
    public static AuthenticationManager.AuthResult checkAccountAuth(KeycloakSession session) {
        AuthenticationManager.AuthResult auth;
        try{
            auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        } catch (Exception e){
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }
        if (auth == null) {
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }

        if(auth.getClient().isServiceAccountsEnabled()) {

            if(!auth.getUser().getUsername().contains("service-account")){
                throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
            }
            else if (auth.getToken().getIssuedFor() == null || !auth.getToken().hasAnyAudience(List.of("identity"))) {
                throw new ErrorResponseException("Invalid Token" ,Response.Status.UNAUTHORIZED);
            }
            else if(!auth.getToken().getResourceAccess().get("identity").getRoles().contains(ACCOUNT_UPDATE_ROLE)){
                throw new ErrorResponseException("Invalid Token" ,Response.Status.UNAUTHORIZED);
            }
        }

        return auth;
    }

    public static AuthenticationManager.AuthResult checkSvcAccountAuth(KeycloakSession session) {
        AuthenticationManager.AuthResult auth;
        try{
            auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
        } catch (Exception e){
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }
        if (auth == null) {
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }

        if(!auth.getClient().isServiceAccountsEnabled() || !auth.getUser().getUsername().contains("service-account")){
            throw new ErrorResponseException("Invalid Token", Response.Status.UNAUTHORIZED);
        }
        else if(auth.getToken().getIssuedFor() == null || !auth.getToken().hasAnyAudience(List.of("identity"))){
            throw new ErrorResponseException("Invalid Token" ,Response.Status.UNAUTHORIZED);
        }
        else if(!auth.getToken().getResourceAccess().get("identity").getRoles().contains(ACCOUNT_READ_ROLE)){
            throw new ErrorResponseException("Invalid Token" ,Response.Status.UNAUTHORIZED);
        }
        return auth;

    }

}

package org.bhn.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.common.ClientConnection;
import org.keycloak.events.*;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.*;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.util.JsonSerialization;

import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author bthiy00
 */
@Slf4j
public class RevokeTokenListener implements EventListenerProvider {

	private final KeycloakSession session;
	private final ClientConnection clientConnection;
	private final HttpHeaders headers;
	private final RealmModel realm;

	private static final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";
	private static final String APPLE_IDENTITY_PROVIDER_ALIAS = "apple";

	public RevokeTokenListener(KeycloakSession session) {

		this.session = session;
		this.clientConnection = session.getContext().getConnection();
		this.realm = session.getContext().getRealm();
		this.headers = session.getContext().getRequestHeaders();
	}

	@Override
	public void onEvent(Event event) {
		if (EventType.REVOKE_GRANT.equals(event.getType())) {

			log.info("revoking token for user id: {} ", event.getUserId());

			UserModel user = session.users().getUserById(session.getContext().getRealm(),event.getUserId());

			if(user == null){
				log.info("user not exist for token revoke user id: {} ", event.getUserId());
				return;
			}

			FederatedIdentityModel federatedIdentityModel = session.users().getFederatedIdentity(realm, user, APPLE_IDENTITY_PROVIDER_ALIAS);

			if(federatedIdentityModel == null || federatedIdentityModel.getToken() == null){
				log.info("federated user not exist for token revoke user id: {} ", event.getUserId());
				return;
			}

			String brokerUserId = APPLE_IDENTITY_PROVIDER_ALIAS + "." + federatedIdentityModel.getUserId(); //"001375.fdbeb91d55904af999e7724cc288dfc5.0452";

			try {
				AccessTokenResponse tokenResponse = JsonSerialization.readValue(federatedIdentityModel.getToken(), AccessTokenResponse.class);

				session.sessions().getUserSessionByBrokerUserIdStream(realm, brokerUserId)
						.collect(Collectors.toList())
						.forEach(userSession -> {
                            try {
                                revokeToken(user, tokenResponse);
								AuthenticationManager.backchannelLogout(session, realm, userSession, session.getContext().getUri(), clientConnection, headers, false, true);
                            } catch (JWSInputException e) {
								log.info("Error during revoke token, message: {}, user_id: {}", e.getMessage(), user.getId());
                                throw new RuntimeException(e);
                            }
                        });

			} catch (Exception e) {
				log.info("Error during revoke token, message: {}, user_id: {}", e.getMessage(), user.getId());
				throw new RuntimeException(e);
			}
        }
	}

	private void revokeToken(UserModel user, AccessTokenResponse token) throws JWSInputException {

		IdentityProviderModel identityProviderModel = session.getContext().getRealm().getIdentityProviderByAlias(APPLE_IDENTITY_PROVIDER_ALIAS);

		String clientId = identityProviderModel.getConfig().get("clientId"); //"IAMPP-Service"
		String clientSecret = identityProviderModel.getConfig().get("clientSecret"); //"eyJraWQiOiJYQUJETlRSODM4IiwiYWxnIjoiRVMyNTYifQ.eyJpc3MiOiI4VzRFWkUyNVY2IiwiaWF0IjoxNzIxMTI4NTYyLCJleHAiOjE3MzY2ODA1NjIsImF1ZCI6Imh0dHBzOi8vYXBwbGVpZC5hcHBsZS5jb20iLCJzdWIiOiJJQU1QUC1TZXJ2aWNlIn0.TaALaFOp7moyop2R6_K5EylrkgrIHBhs6hN9h48UWpQAJtGG2lGJk-QRQ0j5f8jahP1fNLua6lq7QXwAaVTe7Q"

		if(clientId == null || clientSecret == null){
			log.info("clientId/clientSecret not configured for token revoke user id: {} ", identityProviderModel.getAlias());
			return;
		}

//		IDToken idToken = getIdToken(token);
//		String APPLE_REVOKE_URL =  idToken.issuedFor + APPLE_REVOKE_URL_PATH;

		SimpleHttp simpleHttp = SimpleHttp.doPost(APPLE_REVOKE_URL, session)
				.param(OAuth2Constants.CLIENT_ID, clientId)
				.param(OAuth2Constants.CLIENT_SECRET, clientSecret)
				.param("token", token.getRefreshToken())
				.param("token_type_hint", AbstractOAuth2IdentityProvider.OAUTH2_GRANT_TYPE_REFRESH_TOKEN);

		try {
			int revokeResponseStatus = simpleHttp.asStatus();

			if(revokeResponseStatus >= 200 && revokeResponseStatus < 300){
				log.info("successfully revoked token from external IDP(Apple) user id: {} ", user.getId());
			}
		} catch (IOException e) {
			log.info("Error during revoke token,  message: {}, user_id: {}", e.getMessage(), user.getId());
			throw new RuntimeException(e);
		}
	}

	private IDToken getIdToken(AccessTokenResponse tokenResponse) throws JWSInputException {
		JWSInput input = new JWSInput(tokenResponse.getIdToken());
		return input.readJsonContent(IDToken.class);
	}

	@Override
	public void onEvent(AdminEvent event, boolean includeRepresentation) {

	}

	@Override
	public void close() {

	}
}

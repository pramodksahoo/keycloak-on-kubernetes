package org.bhn.resource;

import lombok.extern.slf4j.Slf4j;
import org.bhn.actiontoken.ActionTokenRequest;
import org.bhn.actiontoken.ActionTokenService;
import org.bhn.feignclient.PersonManagementClient;
import org.bhn.resource.model.Account;
import org.bhn.resource.model.UserUpdateModel;
import org.bhn.resource.services.*;
import org.keycloak.common.util.ResponseSessionTask;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @author Vinod Atwal
 */

@Slf4j
public class UserResourceProvider implements RealmResourceProvider {

    private static final int attemptCount = 5;
    private static final int retryIntervalMillis = 100;

    private final KeycloakSession session;
    private final PersonManagementClient personManagementClient;
    private final AESEncryptionService encryptionService;
    private final HttpRequest request;
    private final ClientModel client;

    public UserResourceProvider(KeycloakSession session, PersonManagementClient personManagementClient, AESEncryptionService encryptionService) {
        this.session = session;
        this.personManagementClient = personManagementClient;
        this.encryptionService = encryptionService;
        this.request = session.getContext().getHttpRequest();
        this.client = session.getContext().getClient();
    }


    @Override
    public Object getResource() {
        return this;
    }

    @Override
    public void close() {
    }

    @POST
    @Path("signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signUp() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                RegistrationService registrationService = new RegistrationService(session, personManagementClient);
                return registrationService.process();
            }
        }, attemptCount, retryIntervalMillis);

    /*    RegistrationService registrationService = new RegistrationService(session, personManagementClient);
        return registrationService.process();*/
    }

    @POST
    @Path("signin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signIn() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                SignInService signInService = new SignInService(session, encryptionService);
                return signInService.authenticate();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("forget-password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response forgetPassword() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                ForgetPasswordService forgetPasswordService = new ForgetPasswordService(session, encryptionService);
                return forgetPasswordService.forgetPassword();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("reset-password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response resetPassword() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                ForgetPasswordService forgetPasswordService = new ForgetPasswordService(session, encryptionService);
                return forgetPasswordService.resetPassword();
            }
        }, attemptCount, retryIntervalMillis);
    }


    @POST
    @Path("logout")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logoutUser() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                LogoutService logoutService = new LogoutService(session);
                return logoutService.processLogout();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("update-password/otp")
    public Response updatePasswordOtp() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UpdatePasswordService updatePasswordService = new UpdatePasswordService(session, encryptionService);
                return updatePasswordService.sendOtp();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @PUT
    @Path("update-password")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response updatePassword() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UpdatePasswordService updatePasswordService = new UpdatePasswordService(session, encryptionService);
                return updatePasswordService.updatePassword();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("verify-email/otp")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response verifyEmailOTP() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                VerifyEmailService verifyEmailService = new VerifyEmailService(session, encryptionService);
                return verifyEmailService.sendOtp();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("verify-email")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response verifyEmail() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                VerifyEmailService verifyEmailService = new VerifyEmailService(session, encryptionService);
                return verifyEmailService.verifyEmail();
            }
        }, attemptCount, retryIntervalMillis);
    }


    @POST
    @Path("sso")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response socialSIgnIn() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                SSOService ssoService = new SSOService(session, personManagementClient);
                return ssoService.process();
            }
        }, attemptCount, retryIntervalMillis);
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    @Path("update-user")
    public Response updateUser(UserUpdateModel userData) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UserUpdateService userUpdateService = new UserUpdateService(session);
                return userUpdateService.updateUserData(userData);
            }
        }, attemptCount, retryIntervalMillis);

    }

    @Consumes(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("update-user")
    public Response removeUserMetaData(UserUpdateModel userData) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UserUpdateService userUpdateService = new UserUpdateService(session);
                return userUpdateService.removeUserData(userData);
            }
        }, attemptCount, retryIntervalMillis);

    }

    @GET
    @Path("check-client")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkClient(@QueryParam("client_id") String clientId, @QueryParam("realm") String realm) {
        RealmModel realmModel = session.realms().getRealmByName(realm);
        if (realmModel == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("realm not found, please verify the name").build();
        }
        ClientModel clientModel = realmModel.getClientByClientId(clientId);
        if (clientModel == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("client not found, please verify the name").build();
        }
        return Response.ok(Map.of("clientId", clientModel.getClientId(), "realmName", realmModel.getName())).build();
    }

    @POST
    @Path("action-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTokenLink(ActionTokenRequest payload) {

        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                ActionTokenService ats = new ActionTokenService(session);
                return ats.createActionToken(payload);
            }
        }, attemptCount, retryIntervalMillis);
    }


    @Path("resend-otp")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response resendOTP() {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                return new ResendOtpService(session, encryptionService).generateOtp();
            }
        }, attemptCount, retryIntervalMillis);

    }
/*
   @deprecated
   not in use
    @POST
    @Path("internal-token")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response token(HashMap<String, List<String>> data) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                AnonymousTokenGenerator anonymousTokenGenerator = new AnonymousTokenGenerator(session);
                return anonymousTokenGenerator.generateToken(data);
            }
        }, attemptCount, retryIntervalMillis);

    }*/

    @GET
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserAccount(@QueryParam("email") String email) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UserUpdateService userUpdateService = new UserUpdateService(session);
                return userUpdateService.getUserAccount(email);
            }
        }, attemptCount, retryIntervalMillis);
    }

    @POST
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserAccount(Account account) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                UserUpdateService userUpdateService = new UserUpdateService(session);
                return userUpdateService.createUserAccount(account);
            }
        }, attemptCount, retryIntervalMillis);
    }

    @PUT
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserAccount(Account account) {
        return KeycloakModelUtils.runJobInRetriableTransaction(session.getKeycloakSessionFactory(), new ResponseSessionTask(session) {
            @Override
            public Response runInternal(KeycloakSession session) {
                return new UserUpdateService(session).updateUserAccount(account);
            }
        }, attemptCount, retryIntervalMillis);
    }
}

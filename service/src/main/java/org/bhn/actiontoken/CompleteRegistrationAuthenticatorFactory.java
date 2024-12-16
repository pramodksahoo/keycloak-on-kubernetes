package org.bhn.actiontoken;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AutoService(AuthenticatorFactory.class)
public class CompleteRegistrationAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "complete-registration-authenticator";
    @Override
    public Authenticator create(KeycloakSession session) {
        return new CompleteRegistrationAuthenticator();
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

    @Override
    public String getDisplayType() {
        return "Complete Registration Authenticator";
    }



    @Override
    public String getReferenceCategory() {
        return "";
    }

    @Override
    public String getHelpText() {
        return "Complete Registration Authenticator";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return  new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return  Collections.emptyList();
    }

}

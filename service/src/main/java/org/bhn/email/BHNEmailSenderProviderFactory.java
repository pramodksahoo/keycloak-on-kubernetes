package org.bhn.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.Config;
import org.keycloak.email.EmailSenderProvider;
import org.keycloak.email.EmailSenderProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ServerInfoAwareProviderFactory;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Vinod Atwal
 */
public class BHNEmailSenderProviderFactory implements EmailSenderProviderFactory, ServerInfoAwareProviderFactory {

    private final Map<String, String> configMap = new HashMap<>();
    static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public EmailSenderProvider create(KeycloakSession session) {
        return new BHNEmailSenderProvider(objectMapper);
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
        return "email-extension";
    }

    @Override
    public Map<String, String> getOperationalInfo() {
        return configMap;
    }


}

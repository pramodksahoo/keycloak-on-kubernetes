package org.bhn.credential;

import org.keycloak.Config;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;


public class GCPasswordHashProviderFactory implements PasswordHashProviderFactory {
    public static final String ID = "gc-aes";
    public static final int DEFAULT_ITERATIONS = 180000;

    @Override
    public PasswordHashProvider create(KeycloakSession session) {
        return new GCPasswordHashProvider(ID, DEFAULT_ITERATIONS);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void close() {
    }
}
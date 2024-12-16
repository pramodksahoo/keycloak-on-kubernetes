package org.bhn.credential;

import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class MPCPasswordHashProvider implements PasswordHashProvider {

    private final int defaultIterations;
    private final String providerId;
    private final BCryptPasswordEncoder encoder;

    public MPCPasswordHashProvider(final String providerId, final int defaultIterations, int strength) {

        this.providerId = providerId;
        this.defaultIterations = defaultIterations;
        this.encoder = new BCryptPasswordEncoder(strength);
    }

    @Override
    public boolean policyCheck(PasswordPolicy passwordPolicy, PasswordCredentialModel passwordCredentialModel) {
        final int policyHashIterations = passwordPolicy.getHashIterations() == -1 ? defaultIterations : passwordPolicy.getHashIterations();

        return passwordCredentialModel.getPasswordCredentialData().getHashIterations() == policyHashIterations
                && providerId.equals(passwordCredentialModel.getPasswordCredentialData().getAlgorithm());
    }

    @Override
    public PasswordCredentialModel encodedCredential(String rawPassword, int iterations) {
        return null;
    }

    @Override
    public String encode(String rawPassword, int iterations) {
        return null;
    }


    @Override
    public boolean verify(String rawPassword, PasswordCredentialModel passwordCredentialModel) {
        String storedHashedPassword = passwordCredentialModel.getPasswordSecretData().getValue();
        return encoder.matches(rawPassword, storedHashedPassword);
    }


    @Override
    public void close() {

    }

}
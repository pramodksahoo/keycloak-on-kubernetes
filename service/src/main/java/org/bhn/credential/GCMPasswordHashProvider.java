package org.bhn.credential;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;
import java.security.*;


@Slf4j
public class GCMPasswordHashProvider implements PasswordHashProvider {

    private final int defaultIterations;
    private final String providerId;

    private final static String ENC_ALGO = "SHA";

    public GCMPasswordHashProvider(final String providerId, final int defaultIterations) {

        this.providerId = providerId;
        this.defaultIterations = defaultIterations;
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


        try {

            String encryptedPassword = encrypt(rawPassword);

            return encryptedPassword.toLowerCase().equals(passwordCredentialModel.getPasswordSecretData().getValue());

        }
        catch (Exception e) {
            log.error("Error during password verification" + e.getMessage());
            return false;
        }

    }

    private static String encrypt(String password) {
        byte[] unencodedPassword = password.getBytes();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ENC_ALGO);
        } catch (Exception e) {
            log.error("Error during password encryption" + e.getMessage());
            return password;
        }
        md.reset();
        md.update(unencodedPassword);
        byte[] encodedPassword = md.digest();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < encodedPassword.length; i++) {
            if ((encodedPassword[i] & 0xff) < 0x10) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Long.toString(encodedPassword[i] & 0xff, 16));
        }
        return stringBuffer.toString();
    }


    @Override
    public void close() {

    }

}
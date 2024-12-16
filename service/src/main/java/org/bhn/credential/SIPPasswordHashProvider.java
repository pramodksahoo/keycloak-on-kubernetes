package org.bhn.credential;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SIPPasswordHashProvider implements PasswordHashProvider {

    private static final String transformation = "AES/ECB/PKCS5Padding";
    private static final String algorithm = "AES";
    private final int defaultIterations;
    private final String providerId;


    public SIPPasswordHashProvider(final String providerId, final int defaultIterations) {

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
            String secretData = passwordCredentialModel.getSecretData();
            JSONObject jsonObject = new JSONObject(secretData);
            String base64Salt = jsonObject.getString("salt");
            String encryptedPasswordBase64 = jsonObject.getString("value");
            String encryptedHex = encryptToHex(rawPassword, base64Salt);

            return encryptedPasswordBase64.equals(encryptedHex);
        } catch (Exception e) {
            log.error("Error occurred while verifying password", e);
            return false;
        }
    }

    public static String encryptToHex(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encryptedBytes);
    }

    public static String bytesToHex(byte[] bytes) {
        return Hex.encodeHexString(bytes).toUpperCase(); // Use uppercase to match MySQL output
    }

    @Override
    public void close() {

    }


}
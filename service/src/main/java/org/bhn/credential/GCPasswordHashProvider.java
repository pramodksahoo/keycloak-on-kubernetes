package org.bhn.credential;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class GCPasswordHashProvider implements PasswordHashProvider {

    private final int defaultIterations;
    private final String providerId;

    private  static final String ECB = "AES/ECB/NoPadding";

    public GCPasswordHashProvider(final String providerId, final int defaultIterations) {

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

        String secretSalt = this.generateSecretKey(new String(passwordCredentialModel.getPasswordSecretData().getSalt()));

        try {

            byte[] encryptedPassword = encrypt(rawPassword, secretSalt);

            return Hex.encodeHexString(encryptedPassword).equals(passwordCredentialModel.getPasswordSecretData().getValue());

        }
        catch (IOException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException |
               InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] encrypt(String rawPassword, String secretSalt) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {

        try {

            SecretKeySpec secretKey = new SecretKeySpec(secretSalt.getBytes(), "AES");

            char padChar = (char) (16 - (rawPassword.length() % 16));

            String padValue = StringUtils.rightPad(rawPassword, (int) ((Math.floor(rawPassword.length() / 16) + 1) * 16), padChar);

            Cipher cipher = Cipher.getInstance(ECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return cipher.doFinal(padValue.getBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected String generateSecretKey(String secretSalt) {

        char[] newSecretSalt = new char[16];

        for (int i = 0, len = secretSalt.length(); i < len; i++) {
            newSecretSalt[i % 16] = (char) (newSecretSalt[i % 16] ^ secretSalt.charAt(i));
        }

        return String.valueOf(newSecretSalt);
    }


    @Override
    public void close() {

    }

}
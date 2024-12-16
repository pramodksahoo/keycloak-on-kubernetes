package org.bhn.actiontoken;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.authentication.actiontoken.DefaultActionToken;

import java.util.UUID;

import static org.bhn.resource.constants.Constants.REGISTER_ACTION_TOKEN;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterActionToken extends DefaultActionToken {
    public static final String TOKEN_TYPE = REGISTER_ACTION_TOKEN;

    private String redirectUri;
    private boolean reuse;
    private String scope;
    private String state;

    public RegisterActionToken(String userId,String email ,String clientId, int expirationInSeconds, boolean reuse,
                             String redirectUri, String scope, String nonce, String state) {
        super(userId, TOKEN_TYPE, expirationInSeconds, uuidOf(nonce));
        super.setEmail(email);
        this.issuedFor = clientId;
        this.redirectUri = redirectUri;
        this.reuse = reuse;
        this.scope = scope;
        this.state = state;
    }

    static UUID uuidOf(String s) {
        try {
            return UUID.fromString(s);
        } catch (Exception ignored) {
        }
        return null;
    }
}

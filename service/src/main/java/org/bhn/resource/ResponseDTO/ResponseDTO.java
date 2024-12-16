package org.bhn.resource.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessTokenResponse;

@Data
@Builder
public class ResponseDTO {

    private String userId;
    private String email;
    private String accessToken;
    private long expiresIn;
    private long refreshExpiresIn;
    private String refreshToken;
    private String tokenType;
    private String idToken;
    public String sessionId;
    public String personId;
    public Boolean otp;
    private String emailHash;
    public String name;
    private long timeStamp;


    public static ResponseDTOBuilder builder() {
        return new ResponseDTOBuilder();
    }

    public static class ResponseDTOBuilder {

        public ResponseDTOBuilder setUser(UserModel user) {
            this.userId = user.getId();
            this.email = user.getEmail();
            this.name = user.getFirstName()  + " " +  user.getLastName();
            this.emailHash = DigestUtils.sha256Hex(user.getEmail());
            this.timeStamp = System.currentTimeMillis();
            if(user.getAttributes().get("personId") != null) {
                this.personId = user.getAttributes().get("personId").get(0);
            }

            return this;
        }

        public ResponseDTOBuilder setToken(AccessTokenResponse token) {
            this.accessToken = token.getToken();
            this.refreshToken = token.getRefreshToken();
            this.idToken = token.getIdToken();
            this.expiresIn = token.getExpiresIn();
            this.refreshExpiresIn = token.getRefreshExpiresIn();
            this.tokenType = token.getTokenType();

            return this;
        }

        public ResponseDTOBuilder setSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

    }

}
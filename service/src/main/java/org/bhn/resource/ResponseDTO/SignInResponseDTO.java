package org.bhn.resource.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessTokenResponse;
import software.amazon.awssdk.services.sqs.endpoints.internal.Value;


@Data
@Getter
@Builder
public class SignInResponseDTO {

    public String userId;
    public String email;
    public String sessionId;
    public String personId;
    public Boolean otp;
    public String emailHash;
    public String name;
    public long timeStamp;
    public boolean socialSignUp;

    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("expires_in")
    protected long expiresIn;

    @JsonProperty("refresh_expires_in")
    protected long refreshExpiresIn;

    @JsonProperty("refresh_token")
    protected String refreshToken;

    @JsonProperty("token_type")
    protected String tokenType;

    @JsonProperty("id_token")
    protected String idToken;


    public static SignInResponseDTOBuilder builder(UserModel userModel) {
        return new SignInResponseDTOBuilder(userModel);
    }

    public static class SignInResponseDTOBuilder {

        SignInResponseDTOBuilder(UserModel userModel) {
            this.userId = userModel.getId();
            this.email = userModel.getEmail();
            this.name = userModel.getFirstName()  + " " +  userModel.getLastName();
            this.emailHash = DigestUtils.sha256Hex(userModel.getEmail());
            this.timeStamp = System.currentTimeMillis();
            if(userModel.getAttributes().get("personId") != null) {
                this.personId = userModel.getAttributes().get("personId").get(0);
            }
        }

        public SignInResponseDTOBuilder token(AccessTokenResponse token) {

            this.accessToken = token.getToken();
            this.refreshToken = token.getRefreshToken();
            this.idToken = token.getIdToken();
            this.expiresIn = token.getExpiresIn();
            this.refreshExpiresIn = token.getRefreshExpiresIn();
            this.tokenType = token.getTokenType();

            return this;
        }
    }

}

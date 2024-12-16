package org.bhn.resource.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessTokenResponse;


@Data
@Getter
@Builder
public class ForgotPasswordResponseDTO {

    public String userId;
    public String email;
    public String sessionId;
    public String personId;
    public String emailHash;
    public String name;
    public long timeStamp;



    public static ForgotPasswordResponseDTOBuilder builder(UserModel userModel) {
        return new ForgotPasswordResponseDTOBuilder(userModel);
    }

    public static class ForgotPasswordResponseDTOBuilder {

        ForgotPasswordResponseDTOBuilder(UserModel userModel) {

            this.userId = userModel.getId();
            this.email = userModel.getEmail();
            this.name = userModel.getFirstName() + " " + userModel.getLastName();
            this.emailHash = DigestUtils.sha256Hex(userModel.getEmail());
            this.timeStamp = System.currentTimeMillis();
            if(userModel.getAttributes().get("personId") != null) {
                this.personId = userModel.getAttributes().get("personId").get(0);
            }

        }
    }

}

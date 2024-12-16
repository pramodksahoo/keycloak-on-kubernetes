package org.bhn.resource.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;


@Data
@Getter
@Builder
public class ResetPasswordResponseDTO {

    public String userId;
    public String email;
    public String emailHash;
    public String sessionId;
    public String personId;
    public Boolean otp;
    public String name;
    public long timeStamp;

    public static ResetPasswordResponseDTOBuilder builder(UserModel userModel) {
        return new ResetPasswordResponseDTOBuilder(userModel);
    }

    public static class ResetPasswordResponseDTOBuilder {

        ResetPasswordResponseDTOBuilder(UserModel userModel) {

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

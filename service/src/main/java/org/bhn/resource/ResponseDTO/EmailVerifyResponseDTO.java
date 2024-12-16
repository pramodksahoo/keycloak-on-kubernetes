package org.bhn.resource.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;


@Data
@Getter
@Builder
public class EmailVerifyResponseDTO {

    public String userId;
    public String email;
    public String sessionId;
    public String personId;
    public Boolean otp;
    public String emailHash;
    public String name;
    public long timeStamp;



    public static EmailVerifyResponseDTOBuilder builder(UserModel userModel) {
        return new EmailVerifyResponseDTOBuilder(userModel);
    }

    public static class EmailVerifyResponseDTOBuilder {

        EmailVerifyResponseDTOBuilder(UserModel userModel) {

            this.userId = userModel.getId();
            this.email = userModel.getEmail();
            this.name = userModel.getFirstName() + " " + userModel.getLastName();
            this.emailHash = DigestUtils.sha256Hex(userModel.getEmail());
            this.timeStamp = System.currentTimeMillis();
            if(userModel.getAttributes().get("personId") != null) {
                this.personId = userModel.getAttributes().get("personId").get(0);
            }
        }

        public EmailVerifyResponseDTOBuilder email(String email) {
            this.email = email;
            this.emailHash = DigestUtils.sha256Hex(email);
            return this;
        }
    }

}

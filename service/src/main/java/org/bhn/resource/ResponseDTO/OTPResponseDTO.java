package org.bhn.resource.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;


@Data
@Getter
@Builder
public class OTPResponseDTO {

    public String userId;
    public String email;
    public String personId;
    public String emailHash;
    public String sessionId;
    public String name;
    public long timeStamp;

    public static OTPResponseDTOBuilder builder(UserModel userModel) {
        return new OTPResponseDTOBuilder(userModel);
    }

    public static class OTPResponseDTOBuilder {

        OTPResponseDTOBuilder(UserModel userModel) {

            this.userId = userModel.getId();
            this.email = userModel.getEmail();
            this.name = userModel.getFirstName()  + " " + userModel.getLastName();
            this.emailHash = DigestUtils.sha256Hex(userModel.getEmail());
            this.timeStamp = System.currentTimeMillis();
            if(userModel.getAttributes().get("personId") != null) {
                this.personId = userModel.getAttributes().get("personId").get(0);
            }
        }
    }

}

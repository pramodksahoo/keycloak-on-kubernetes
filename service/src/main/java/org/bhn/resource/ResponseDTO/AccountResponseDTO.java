package org.bhn.resource.ResponseDTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.keycloak.models.UserModel;


@Data
@Getter
@Builder
public class AccountResponseDTO {

    public String id;
    public String email;
    public String firstName;
    public String lastName;
    public String personId;
    public Boolean emailVerified;
    public Boolean enabled;
    public Boolean mfa;
    public long createdTimestamp;

    public static AccountResponseDTOBuilder builder(UserModel userModel) {
        return new AccountResponseDTOBuilder(userModel);
    }

    public static class AccountResponseDTOBuilder {

        AccountResponseDTOBuilder(UserModel userModel) {

            this.id = userModel.getId();
            this.email = userModel.getEmail();
            this.firstName = userModel.getFirstName();
            this.lastName = userModel.getLastName();
            this.emailVerified = userModel.isEmailVerified();
            this.createdTimestamp = userModel.getCreatedTimestamp();
            if(userModel.getAttributes().get("personId") != null) {
                this.personId = userModel.getAttributes().get("personId").get(0);
            }
            if(userModel.getAttributes().get("MFA") != null) {
                this.mfa = true;
            }
        }
    }

}

package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bhn.enums.AccountType;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppProfileInDTO {
    @JsonProperty("client_id")
    private String clientId;

    private URL picture;

    private boolean blocked =false;

    @JsonProperty("display_language")
    private String displayLanguage;

    @JsonProperty("two_factor_authentication")
    private Boolean twoFA;

    @JsonProperty("promotion_email")
    private boolean promotionEmail;

    @JsonProperty("account_type")
    private AccountType accountType;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("phone_verified")
    private Boolean phoneVerified;

    @JsonProperty("preferred_contact_method")
    private String preferredContactMethod;
}
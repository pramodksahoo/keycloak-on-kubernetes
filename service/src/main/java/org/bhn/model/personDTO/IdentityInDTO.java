package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bhn.enums.IdentityProvider;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityInDTO {
    private IdentityProvider provider;
    @JsonProperty("realm_name")
    private String realmName;
    @JsonProperty("external_user_id")
    private String externalUserId;
    @JsonProperty("is_social")
    private boolean isSocial = false;
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    @JsonProperty("account_idp")
    private List<String> accountIDP;
}
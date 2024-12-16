package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonCommonInDTO {
    private PersonInDTO person;
    private IdentityInDTO identity;

    @JsonProperty("app_profile")
    private AppProfileInDTO appProfile;

    private ContactInDTO contact;
}

package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseDTO {

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("app_profile_id")
    private String appProfileId;

    @JsonProperty("identity_id")
    private String identityId;

    @JsonProperty("contact_id")
    private String contactId;
}

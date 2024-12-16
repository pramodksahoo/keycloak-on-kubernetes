package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bhn.enums.ContactType;
import org.bhn.enums.PreferredContactMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactInDTO {
    private String city;
    private String country;
    private String email;
    private String phone;
    @JsonProperty("contact_type")
    private ContactType contactType;
    @JsonProperty("address_line_1")
    private String addressLine1;
    @JsonProperty("address_line_2")
    private String addressLine2;
    @JsonProperty("address_line_3")
    private String addressLine3;
    private String state;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("preferred_communication_method")
    private PreferredContactMethod preferredCommunicationMethod;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("self_address")
    private boolean self = true;
    @JsonProperty("display_language")
    private String displayLanguage;
}

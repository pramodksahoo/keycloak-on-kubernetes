package org.bhn.model.personDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bhn.enums.PersonGender;
import org.bhn.enums.PersonTitle;
import org.bhn.enums.PreferredContactMethod;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonInDTO {
    private PersonTitle title;
    private String ssn;
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("email_verified")
    private Boolean emailVerified = false;
    @JsonProperty("phone_verified")
    private Boolean phoneVerified = false;

    private PersonGender gender;
    @JsonProperty("middle_name")
    private String middleName;
    private String phone;
    @JsonProperty("preferred_contact_method")
    private PreferredContactMethod preferredContactMethod = PreferredContactMethod.EMAIL;
    @JsonProperty("display_language")
    private String displayLanguage;
    @JsonProperty("birth_date")
    private String birthDate;
    private String notes;
}

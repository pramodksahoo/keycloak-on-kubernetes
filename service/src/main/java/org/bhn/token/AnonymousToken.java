package org.bhn.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.JsonWebToken;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class AnonymousToken extends JsonWebToken {
    @JsonProperty("sub")
    protected String subject="Anonymous-Token";
    private HashMap<String, List<String>>  data;

    @Override
    public void setSubject(String subject){

    }

}

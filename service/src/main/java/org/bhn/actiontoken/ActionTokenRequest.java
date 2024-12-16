package org.bhn.actiontoken;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionTokenRequest {
    private String clientId;
    private String username;
    private String email;
    private String redirectUri;
    private String scope = "openid";
    private String nonce = null;
    private String state = null;
    private Integer expirationSeconds = null;
    private boolean forceCreate;
    private String[] requiredActions;
    private boolean updateProfile = false;
    private boolean updatePassword = false;
    private boolean reuse = false;
}

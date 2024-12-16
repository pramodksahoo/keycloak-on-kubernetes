package org.bhn.model.event;

import lombok.Data;

import java.util.Map;

@Data
public class SingUpLoginMessage {
    private String eventType;
    private String firstName;
    private String lastName;
    private String lastIp;
    private String appDomain;
    private String displayLanguage;
    private String realmName;
    private String clientId;
    private String externalUserId;
    private long creationDate;
    private Boolean emailVerified;
    private boolean blocked;
    private Boolean isSocial;
    //device name
    private String deviceName;
    private String deviceOs;
    private String deviceOsMajor;
    private String deviceOsMinor;
    private String deviceUserAgent;
    private String deviceUserAgentMajor;
    private String deviceUserAgentMinor;
    private Boolean promotionEmail;
    private Map<String, String> clientAttributes;
}

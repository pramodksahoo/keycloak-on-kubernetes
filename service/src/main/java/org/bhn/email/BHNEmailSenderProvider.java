package org.bhn.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.bhn.resource.services.EmailSender;
import org.keycloak.email.EmailSenderProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vinod Atwal
 */

@Slf4j
public class BHNEmailSenderProvider implements EmailSenderProvider {
    private final ObjectMapper objectMapper;

    public BHNEmailSenderProvider( ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(Map<String, String> config, String address, String subject, String textBody, String htmlBody) {
        try {
            HashMap<String, String> valueMap = objectMapper.readValue(textBody, new TypeReference<HashMap<String, String>>() {});
            if(valueMap.get("template").contains("password-reset")){
                log.info("sending password reset email");
            }else{
                log.info("template might not configured for email {}",valueMap.get("template"));
            }
            String email = valueMap.get("email");
            valueMap.put("to", email);
            EmailSender.internalEmail(valueMap);
        } catch (JsonProcessingException e) {
            log.error("unable to process message payload for internal email sender {}",e.getMessage());
        }
    }

    @Override
    public void close() {
    }
}

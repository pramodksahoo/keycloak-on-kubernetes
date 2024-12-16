package org.bhn.feignclient;

import feign.HeaderMap;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.bhn.model.User;
import org.bhn.model.UserCreationResponse;
import org.bhn.model.personDTO.PersonCommonInDTO;
import org.bhn.model.personDTO.PersonResponseDTO;

import java.util.Map;

/**
 * @author Vinod Atwal
 */
public interface PersonManagementClient {


    @RequestLine("POST /v1/persons")
    @Headers({"Content-Type: application/json", "X-REGISTERED: TRUE", "X-TENANT-ID: {clientId}", "X-REALM-NAME: {realmName}"})
    PersonResponseDTO createPerson(PersonCommonInDTO person, @Param("clientId") String clientId, @Param("realmName") String realmName);

    @Headers({"Content-Type: application/json", "X-REGISTERED: TRUE"})
    @RequestLine("POST /persons")
    UserCreationResponse create(@HeaderMap Map<String, Object> headers, User user);

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /persons/{id}")
    User update(User user, @Param("id") String id);
}

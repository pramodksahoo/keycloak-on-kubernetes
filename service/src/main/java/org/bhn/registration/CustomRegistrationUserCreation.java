//package org.bhn.registration;
//
//import lombok.extern.slf4j.Slf4j;
//import com.google.auto.service.AutoService;
//import org.bhn.feignclient.FeignClientBuilder;
//import org.bhn.feignclient.PersonManagementClient;
//import org.bhn.model.CustomConstant;
//import org.bhn.model.User;
//import org.bhn.model.UserCreationResponse;
//import org.bhn.utils.Utils;
//import org.keycloak.authentication.FormActionFactory;
//import org.keycloak.authentication.FormContext;
//import org.keycloak.authentication.forms.RegistrationUserCreation;
//import org.keycloak.forms.login.LoginFormsProvider;
//import org.keycloak.services.ErrorPageException;
//
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MultivaluedMap;
//import javax.ws.rs.core.Response;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.bhn.model.CustomConstant.HEADER_CUSTOM_TYPE;
//import static org.bhn.model.CustomConstant.HEADER_REGISTERED;
//
//
//@AutoService(FormActionFactory.class)
//@Slf4j
//public class CustomRegistrationUserCreation extends RegistrationUserCreation {
//
//    private final PersonManagementClient client;
//
//    public CustomRegistrationUserCreation() {
//        this.client = FeignClientBuilder.getPersonManagementClient();
//    }
//
//
//    @Override
//    public void success(FormContext context) {
//        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
//        HttpHeaders headers = context.getHttpRequest().getHttpHeaders();
//
//        try {
//            User requestBody = User.builder()
//                    .username(getFormValue(formData, CustomConstant.USER_NAME))
//                    .first_name(getFormValue(formData, CustomConstant.FIRST_NAME))
//                    .last_name(getFormValue(formData, CustomConstant.LAST_NAME))
//                    .display_language(Utils.getDisplayLanguage(headers, "Accept-Language"))
//                    .email(getFormValue(formData, CustomConstant.EMAIL))
//                    .build();
//
//            Map<String, Object> customHeaders = new HashMap<>();
//            customHeaders.put(HEADER_CUSTOM_TYPE, "application/json");
//            customHeaders.put(HEADER_REGISTERED, true);
//
//            log.info("Sending request to person {} service: {}", requestBody.getEmail(), requestBody);
//            UserCreationResponse response = client.create(customHeaders, requestBody);
//            formData.add(CustomConstant.ATTRIBUTE_PERSON_ID, response.getPerson_id());
//            log.info("Person was saved successfully with id: {}", response.getPerson_id());
//        } catch (Exception ex) {
//            log.error("Error during person service user creation request, user email {}", getFormValue(formData, CustomConstant.EMAIL));
//            throw new ErrorPageException(context.getSession(), Response.Status.BAD_REQUEST, "Error during person service user creation request");
//        }
//        super.success(context);
//    }
//
//    @Override
//    public void buildPage(FormContext context, LoginFormsProvider form) {
//
//    }
//
//    private String getFormValue(MultivaluedMap<String, String> formData, String attribute) {
//        if (formData.containsKey(attribute)) {
//            return formData.get(attribute).get(0);
//        }
//        return null;
//    }
//
//}

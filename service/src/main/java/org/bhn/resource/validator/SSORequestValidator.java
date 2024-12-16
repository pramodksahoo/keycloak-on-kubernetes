package org.bhn.resource.validator;//package org.bhn.resource.validator;
//
//import org.bhn.resource.model.SSORequest;
//import org.keycloak.models.RealmModel;
//import org.keycloak.models.utils.FormMessage;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SSORequestValidator {
//    public static List<FormMessage> errors =null;
//    public static List<FormMessage> validate(SSORequest request, RealmModel realm) {
//        errors = new ArrayList<>();
//        validateField("code",request.getCode());
//        validateProviderId("providerId",request.getProviderId(),realm);
//        validateClientId(request.getClientId(),realm);
//        return errors;
//    }
//    public static void validateProviderId(String fieldName, String value,RealmModel realm){
//        if(value == null || value.isEmpty() || realm.getIdentityProviderByAlias(value)==null){
//            errors.add(new FormMessage(fieldName,"Invalid input"));
//        }
//    }
//
//    private static void validateField(String fieldName, String value) {
//        if (value == null || value.isEmpty()) {
//            errors.add(new FormMessage(fieldName,"is not valid input"));
//        }
//    }
//
//    private static void validateClientId(String value, RealmModel realm){
//        if(value == null || value.isEmpty() || realm.getClientByClientId(value)==null){
//            errors.add(new FormMessage("clientId","Invalid input"));
//        }
//    }
//
//}

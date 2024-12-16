package org.bhn.resource.validator;

import org.bhn.resource.model.UserUpdateModel;
import org.keycloak.models.utils.FormMessage;

import java.util.ArrayList;
import java.util.List;

public class UserUpdateModelValidator {
    public static List<FormMessage> errors =null;
    public static List<FormMessage> validate(UserUpdateModel userUpdateModel) {
        errors = new ArrayList<>();
        validateField("personId", userUpdateModel.getPersonId());
        validateField("userId", userUpdateModel.getUserId());
        validateField("realmName", userUpdateModel.getRealmName());
        validateField("email", userUpdateModel.getEmail());
        return errors;

    }

    private static void validateField(String fieldName, String value) {
        if (value == null || value.isEmpty()) {
            errors.add(new FormMessage(fieldName,"is not valid input"));
        }
    }

}

package org.bhn.resource.validator;

import com.google.auto.service.AutoService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.keycloak.common.util.Resteasy;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProviderFactory;

//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//import javax.ws.rs.ext.Provider;



public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {


    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext constraintValidatorContext) {
        KeycloakSession session = Resteasy.getContextData(KeycloakSession.class);
        return userName != null && session.users().getUserByEmail(session.getContext().getRealm(), userName) == null;
    }
}
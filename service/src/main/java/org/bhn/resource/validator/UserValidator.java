/*
package org.bhn.resource.validator;


import lombok.RequiredArgsConstructor;
import org.bhn.resource.model.UserRegModel;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserValidator {
    private final KeycloakSession session;

    public  List<FormMessage> isValid(UserRegModel input){
        List<FormMessage> errors = new ArrayList<>();

        if(this.isFirstNameInvalid(input.getFirstName())){
            errors.add(new FormMessage(RegistrationPage.FIELD_FIRST_NAME,"Blank or invalid firstname format"));
        }

        if(!this.isLastNameValid(input.getLastName())){
            errors.add(new FormMessage(RegistrationPage.FIELD_LAST_NAME,"Blank or invalid lastname format"));
        }

        if(!this.isEmailValid(input.getEmail())){
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL,"Blank or invalid email format"));
        }else if(this.isUserAlreadyExists(input.getEmail())){
            errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.EMAIL_EXISTS));
        }

        if(!this.isPasswordValid(input.getPassword(), input.getConfirmPassword())){
            errors.add(new FormMessage(RegistrationPage.FIELD_PASSWORD,"Password didn't match or is invalid"));
        }else{
            PolicyError err = session.getProvider(PasswordPolicyManagerProvider.class).validate(session.getContext().getRealm().isRegistrationEmailAsUsername() ? input.getEmail() : null, input.getPassword());
            if (err != null)
                errors.add(new FormMessage(RegistrationPage.FIELD_PASSWORD, err.getMessage(), err.getParameters()));
        }

        if(!this.isClientIdValid(input.getClientId())){
            errors.add(new FormMessage("client_id","Invalid Client Id"));
        }

       */
/* if(!this.isMfaValid(input.getMfa())){
            errors.add(new FormMessage("mfa","mfa required field, cannot be null"));
        }*//*


        if(!this.isTermAndConditionValid(input.getTermAndCondition())){
            errors.add(new FormMessage("termAndCondition","termAndCondition required field, cannot be null"));
        }



        return errors;
    }

    private boolean isUserAlreadyExists(String email) {

        return session.users().getUserByEmail(session.getContext().getRealm(), email) !=null;
    }

    private boolean isFirstNameInvalid(String firstName){
        if (firstName == null || firstName.trim().isEmpty()) {
            return true;
        }
        else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", firstName)) {
            return true;
        }

        else if (firstName.length()>50){
            return false;
        }else{
            return false;
        }
    }

   */
/* private boolean isMfaValid(Boolean mfa){
        return mfa!=null;
    }
*//*

    private boolean isTermAndConditionValid(Boolean termAndCondition){
        return termAndCondition!=null;
    }

    private boolean isLastNameValid(String lastName){
        if (lastName == null || lastName.trim().isEmpty()) {
            return false;
        }

        if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", lastName)) {
            return false;
        }

        if(lastName.length()>50){
            return false;
        }

        return true;

    }

    private boolean isEmailValid(String email){
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!Validation.isEmailValid(email) ||email.length()>50 ) {
            return false;
        }

        return true;

    }

    private boolean isPasswordValid(String password, String confirmPassword){
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return false;
        }
        if(!password.equals(confirmPassword)){
            return false;
        }
        return true;
    }

    private boolean isClientIdValid(String clientId){
        if(clientId == null || clientId.trim().isEmpty()){
            return false;
        }else{
            return session.clients().getClientByClientId(session.getContext().getRealm(),clientId)!=null;
        }
    }

 }
*/

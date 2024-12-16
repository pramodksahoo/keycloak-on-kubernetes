package org.bhn.actiontoken;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class CompleteRegistrationAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.isEmpty()) {
            // Display the custom form
            context.challenge(createForm(context));
        } else {
            // Process form submission
            formData = context.getHttpRequest().getDecodedFormParameters();
            String password = formData.getFirst("password");
            String firstName = formData.getFirst("first_name");
            String lastName = formData.getFirst("last_name");
            UserModel user = context.getUser();
            PolicyError err = context.getSession().getProvider(PasswordPolicyManagerProvider.class)
                    .validate(context.getSession().getContext().getRealm(), user, password);
                context.success();
            List<FormMessage> errors = new ArrayList<>();
            if(err!=null){
                errors.add(new FormMessage("password", "PasswordInvalid"));
            }
            if(firstName==null || firstName.isEmpty() || firstName.length()>50){
                errors.add(new FormMessage("first_name", "FirstName Invalid"));
            }
            if(lastName==null || lastName.isEmpty() || lastName.length()>50){
                errors.add(new FormMessage("last_name", "LastName Invalid"));
            }
            if (!errors.isEmpty()) {
                context.form()
                        .setAttribute("email", formData.getFirst("email")) // Assuming "email" is a field in your form
                        .setAttribute("first_name", formData.getFirst("first_name"))
                        .setAttribute("last_name", formData.getFirst("last_name"));
                errors.forEach(
                        e-> context.challenge(createForm(context,form->form.addError(e)))
                );
                return;

            }
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.credentialManager().updateCredential(UserCredentialModel.password(password));
            // Change to your desired website URL
            String redirectUrl =  context.getAuthenticationSession().getRedirectUri();
            // Use HttpServletResponse to send the redirect response
            context.setForwardedInfoMessage("success","redirect to this website");
            // Indicate// Change to your desired website URL

            // Create a response with the redirect status and location header
            Response response = Response.status(Response.Status.FOUND)
                    .header("Location", redirectUrl)
                    .build();
            Response res = context.form()
                    .setAttribute("redirectUrl", redirectUrl)
                    .createForm("success-message.ftl");
            // Set the response in the context to perform the redirection
            context.forceChallenge(res);
//            context.success();
            }
        }


    @Override
    public void action(AuthenticationFlowContext context) {
        // Process form submission here if needed
        authenticate(context);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {

    }

    private Response createForm(AuthenticationFlowContext context) {
        LoginFormsProvider form = context.form()
                .setAttribute("email", context.getUser().getEmail())
                .setAttribute("first_name", context.getUser().getFirstName())
                .setAttribute("last_name",  context.getUser().getLastName())
                .setAttribute("password","");
        return form.createForm("complete-profile.ftl");
    }

    private Response createForm(AuthenticationFlowContext context, Consumer<LoginFormsProvider> formConsumer) {
        LoginFormsProvider form = context.form()
                .setAttribute("email", context.getUser().getEmail())
                .setAttribute("first_name", context.getUser().getFirstName())
                .setAttribute("last_name",  context.getUser().getLastName())
                .setAttribute("password","");

        if (formConsumer != null) {
            formConsumer.accept(form);
        }

        return form.createForm("complete-profile.ftl");
    }

    @Override
    public void close() {

    }
}

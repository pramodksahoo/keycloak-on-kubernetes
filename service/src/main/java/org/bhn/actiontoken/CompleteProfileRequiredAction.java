package org.bhn.actiontoken;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.authentication.InitiatedActionSupport;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.keycloak.services.validation.Validation;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AutoService(RequiredActionFactory.class)
public class CompleteProfileRequiredAction implements
        RequiredActionFactory, RequiredActionProvider {

    public static final String PROVIDER_ID = "COMPLETE-PROFILE";

    @Override
    public InitiatedActionSupport initiatedActionSupport() {
        return InitiatedActionSupport.SUPPORTED;
    }

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // you would implement something like the following, if this required action should be "self registering" at the user
        // if (context.getUser().getFirstAttribute(PHONE_NUMBER_FIELD) == null) {
        // 	context.getUser().addRequiredAction(PROVIDER_ID);
        //  context.getAuthenticationSession().addRequiredAction(PROVIDER_ID);
        // }
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        // show initial form
        context.challenge(createForm(context));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        // submitted form
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String password = formData.getFirst("password");
        String firstName = formData.getFirst("first_name");
        String lastName = formData.getFirst("last_name");
        UserModel user = context.getUser();
        PolicyError err = context.getSession().getProvider(PasswordPolicyManagerProvider.class)
                .validate(context.getSession().getContext().getRealm(), user, password);
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
            errors.forEach(
                    e-> context.challenge(createForm(context,form->form.addError(e)))
            );
            return;

        }


        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.credentialManager().updateCredential(UserCredentialModel.password(password));
        user.removeRequiredAction(PROVIDER_ID);
        context.getAuthenticationSession().removeRequiredAction(PROVIDER_ID);
        context.success();
    }

    @Override
    public RequiredActionProvider create(KeycloakSession keycloakSession) {
        return this;
    }

    @Override
    public String getDisplayText() {
        return "complete user profile";
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    private Response createForm(RequiredActionContext context) {
        return createForm(context, null);
    }

    private Response createForm(RequiredActionContext context, Consumer<LoginFormsProvider> formConsumer) {
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

}

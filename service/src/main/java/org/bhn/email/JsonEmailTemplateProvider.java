package org.bhn.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.keycloak.email.EmailException;
import org.keycloak.email.freemarker.FreeMarkerEmailTemplateProvider;
import org.keycloak.models.KeycloakSession;

import java.util.List;
import java.util.Map;

/**
 * @author Vinod Atwal
 */
public class JsonEmailTemplateProvider extends FreeMarkerEmailTemplateProvider {

	private final ObjectMapper mapper;

	public JsonEmailTemplateProvider(KeycloakSession session, ObjectMapper mapper) {

		super(session);
		this.mapper = mapper;
	}

	@Override
	protected EmailTemplate processTemplate(String subjectKey, List<Object> subjectAttributes, String template, Map<String, Object> attributes) throws EmailException {
		try {

			attributes.put("realm", realm.getName());
            if(authenticationSession!=null) {
                attributes.put("template", String.format("%s-"+template.replace(".ftl", "")
                        ,authenticationSession.getClient().getName()));
            }
            if(user!=null){
                attributes.put("userData",true);
                attributes.put("firstname",user.getFirstName());
                attributes.put("lastname",user.getLastName());
                attributes.put("email",user.getEmail());
                if(user.getAttributes().get("personId")!=null){
                    attributes.put("personId",user.getAttributes().get("personId").stream().findFirst().get());
                }else{
                    attributes.put("personId","not-binded");
                }

            }
            attributes.remove("linkExpirationFormatter");
            String jsonString = new Gson().toJson(attributes);
			return new EmailTemplate(subjectKey, jsonString, null);
		} catch (Exception e) {
			throw new EmailException("Failed to create JSON output for email", e);
		}
	}

}

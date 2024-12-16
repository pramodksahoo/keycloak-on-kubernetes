package org.bhn.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;

/**
 * @author Vinod Atwal
 */
@Data
@Builder
@ToString
public class User {
	private String id;
	private String person_id;
	private String username;
	private String first_name;
	private String last_name;
	private String email;
	private String display_language;
	private boolean email_verified;
	private String postalCode;
}

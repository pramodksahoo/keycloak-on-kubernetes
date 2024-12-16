package org.bhn.auth.permissions;

import org.keycloak.authorization.AuthorizationProvider;
import org.keycloak.authorization.model.Policy;
import org.keycloak.authorization.model.Resource;
import org.keycloak.authorization.model.ResourceServer;
import org.keycloak.authorization.model.Scope;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RoleModel;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.Logic;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

import java.util.HashMap;
import java.util.Map;

class Helper {
    Helper() {
    }

    public static Policy addScopePermission(AuthorizationProvider authz, ResourceServer resourceServer, String name, Resource resource, Scope scope, Policy policy) {
        ScopePermissionRepresentation representation = new ScopePermissionRepresentation();
        representation.setName(name);
        representation.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
        representation.setLogic(Logic.POSITIVE);
        representation.addResource(resource.getName());
        representation.addScope(new String[]{scope.getName()});
        representation.addPolicy(new String[]{policy.getName()});
        return authz.getStoreFactory().getPolicyStore().create(resourceServer, representation);
    }

    public static Policy addEmptyScopePermission(AuthorizationProvider authz, ResourceServer resourceServer, String name, Resource resource, Scope scope) {
        ScopePermissionRepresentation representation = new ScopePermissionRepresentation();
        representation.setName(name);
        representation.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
        representation.setLogic(Logic.POSITIVE);
        representation.addResource(resource.getName());
        representation.addScope(new String[]{scope.getName()});
        return authz.getStoreFactory().getPolicyStore().create(resourceServer, representation);
    }

    public static Policy createRolePolicy(AuthorizationProvider authz, ResourceServer resourceServer, RoleModel role) {
        String roleName = getRolePolicyName(role);
        return createRolePolicy(authz, resourceServer, role, roleName);
    }

    public static Policy createRolePolicy(AuthorizationProvider authz, ResourceServer resourceServer, RoleModel role, String policyName) {
        PolicyRepresentation representation = new PolicyRepresentation();
        representation.setName(policyName);
        representation.setType("role");
        representation.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
        representation.setLogic(Logic.POSITIVE);
        String roleValues = "[{\"id\":\"" + role.getId() + "\",\"required\": true}]";
        Map<String, String> config = new HashMap();
        config.put("roles", roleValues);
        representation.setConfig(config);
        return authz.getStoreFactory().getPolicyStore().create(resourceServer, representation);
    }

    public static String getRolePolicyName(RoleModel role) {
        String roleName = "";
        if (role.getContainer() instanceof ClientModel) {
            ClientModel client = (ClientModel)role.getContainer();
            roleName = client.getClientId() + "." + role.getName();
        } else {
            roleName = role.getName();
        }

        roleName = "role.policy." + roleName;
        return roleName;
    }
}
